package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ApplyConferenceRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.ConferenceNameDuplicatedException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.ThesisRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private ThesisRepository thesisRepository;

    public ConferenceService(AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, ThesisRepository thesisRepository) {
        this.authorityRepository = authorityRepository;
        this.conferenceRepository = conferenceRepository;
        this.thesisRepository = thesisRepository;
    }

    public Conference applyConference(ApplyConferenceRequest request) throws BadCredentialsException, ConferenceNameDuplicatedException {
        String fullName = request.getFullName();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        Conference conference = conferenceRepository.findByFullName(request.getFullName());
        if (conference != null) throw new ConferenceNameDuplicatedException(request.getFullName());
        conference = new Conference(request.getAbbreviation(), fullName, request.getPlace(), request.getStartDate(), request.getEndDate(), request.getDeadline(), request.getReleaseTime(), userRepository.findByUsername(userDetails.getUsername()));
        conferenceRepository.save(conference);
        return conference;
    }

    public Set<Conference> listConferences(String text) throws BadCredentialsException {
        if ("admin".equals(text)) return conferenceRepository.findAllByApplying(true);
        else if ("submission".equals(text)) return conferenceRepository.findAllByValid(true);
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

    public Thesis submitThesis(String conferenceFullName, String title, String summary, MultipartFile file) throws BadCredentialsException, IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        File target = new File(ResourceUtils.getURL("classpath:").getPath(), "static/" + conferenceFullName + "/" + user.getUsername() + "/");
        if (!target.exists()) target.mkdirs();
        StringBuilder path = new StringBuilder(target.getAbsolutePath() + title);
        while (new File(path + ".pdf").exists()) path.append("(1)");
        String thesisPath = path + ".pdf";
        FileOutputStream out = new FileOutputStream(thesisPath);
        out.write(file.getBytes());
        out.flush();
        out.close();
        Thesis thesis = new Thesis(title, user, conferenceFullName, summary, thesisPath);
        thesisRepository.save(thesis);
        return thesis;
    }
}
