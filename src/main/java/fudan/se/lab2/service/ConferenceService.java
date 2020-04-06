package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ApplyConferenceRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.ConferenceNameDuplicatedExecption;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author YHT
 */
@Service
public class ConferenceService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private ConferenceRepository conferenceRepository;

    public ConferenceService(AuthorityRepository authorityRepository,ConferenceRepository conferenceRepository){
        this.authorityRepository=authorityRepository;
        this.conferenceRepository=conferenceRepository;
    }

    public Conference applyConference(ApplyConferenceRequest request) throws BadCredentialsException, ConferenceNameDuplicatedExecption {
        String fullName = request.getFullName();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        Conference conference = conferenceRepository.findByFullName(request.getFullName());
        if (conference != null) throw new ConferenceNameDuplicatedExecption(request.getFullName());
        conference = new Conference(request.getAbbreviation(), fullName, request.getPlace(), request.getStartDate(), request.getEndDate(), request.getDeadline(), request.getReleaseTime(), userRepository.findByUsername(userDetails.getUsername()));
        conferenceRepository.save(conference);
        return conference;
    }

    public Set<?> listConferences(String text) throws BadCredentialsException {
        if ("admin".equals(text)) return conferenceRepository.findAllByApplying(true);
        else if ("user".equals(text)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails == null) throw new BadCredentialsException("Not authorized.");
            Set<Authority> authorities = authorityRepository.findAllByUser(userRepository.findByUsername(userDetails.getUsername()));
            Set<Authority> returnAuthorities = new HashSet<Authority>();
            for (Authority authority : authorities)
                if (authority.getAuthority().equals("Chair") || authority.getAuthority().equals("PC member") || authority.getAuthority().equals("Author"))
                    returnAuthorities.add(authority);
            return returnAuthorities;
        } else if ("submission".equals(text)) return conferenceRepository.findAllByValid(true);
        return null;
    }

    public boolean auditConferenceApplication(String conferenceFullName, boolean passed) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        if (conference == null) return false;
        conference.setApplying(false);
        if (passed) {
            User creator = conference.getCreator();
            conference.setValid(true);
            authorityRepository.save(new Authority("Chair", creator, conference.getFullName()));
        }
        return true;
    }

    public Set<User> searchUsers(String text, String conferenceFullName) {
        Set<User> users = userRepository.findAllByFullNameContaining(text);
        Set<User> resultUsers = new HashSet<User>();
        for (User user : users)
            if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("PC Member", user, conferenceFullName) == null
                    && authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Chair", user, conferenceFullName) == null)
                resultUsers.add(user);
        return resultUsers;
    }

    public boolean invitePCMember(String username, String conferenceFullName) {
        User user = userRepository.findByUsername(username);
        authorityRepository.save(new Authority("Undetermined PC Member", user, conferenceFullName));
        return true;
    }

    public boolean auditPCInvitationApplication(String conferenceFullName, boolean passed) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Set<Authority> authorities = authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("undetermined PC Member", user, conferenceFullName);
        if (authorities == null) throw new BadCredentialsException("Bad operation.");
        Authority authority = authorities.iterator().next();
        if (passed) authority.setAuthority("PC Member");
        else authority.setAuthority("Denied PC Member");
        return true;
    }

    public boolean changeSubmissionState(String conferenceFullName, boolean passed) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        conference.setSubmitting(passed);
        return true;
    }
}
