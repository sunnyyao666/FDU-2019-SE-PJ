package fudan.se.lab2.service;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author YHT
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ConferenceRepository conferenceRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.conferenceRepository = conferenceRepository;
        this.encoder = encoder;
    }

    public User register(RegisterRequest request) throws UsernameHasBeenRegisteredException {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) throw new UsernameHasBeenRegisteredException(username);
        user = new User(username, encoder.encode(request.getPassword()), request.getFullName(), request.getEmail(), request.getOffice(), request.getRegion());
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) throws UsernameNotFoundException, BadCredentialsException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User: '" + username + "' not found.");
        if (!encoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("User: '" + username + "' got wrong password.");
        return user;
    }

    public User updateUser() throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        return userRepository.findByUsername(userDetails.getUsername());
    }

    public Set<User> searchUsers(String text, String conferenceFullName) {
        Set<User> users = userRepository.findAllByFullNameContaining(text);
        Set<User> resultUsers = new HashSet<>();
        for (User user : users)
            if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("PC Member", user, conferenceFullName).isEmpty()
                    && authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Chair", user, conferenceFullName).isEmpty()
                    && authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Admin", user, null).isEmpty())
                resultUsers.add(user);
        return resultUsers;
    }

    public boolean invitePCMember(String username, String conferenceFullName) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User inviter = userRepository.findByUsername(userDetails.getUsername());
        User user = userRepository.findByUsername(username);
        Authority authority = new Authority("Undetermined PC Member", user, conferenceFullName, inviter.getUsername());
        authority.setTopics(conferenceRepository.findByFullName(conferenceFullName).getTopics());
        authorityRepository.save(authority);
        return true;
    }

    public Set<Authority> listInviteHistory(String conferenceFullName) {
        return authorityRepository.findAllByAuthorityContainingAndConferenceFullName("PC Member", conferenceFullName);
    }

    public boolean auditPCInvitationApplication(String conferenceFullName, String topics) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Set<Authority> authorities = authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Undetermined PC Member", user, conferenceFullName);
        if (authorities.isEmpty()) throw new BadCredentialsException("Bad operation.");
        Authority authority = authorities.iterator().next();
        if (topics.equals("false")) authority.setAuthority("Denied PC Member");
        else {
            authority.setAuthority("PC Member");
            authority.setTopics(topics);
        }
        authorityRepository.save(authority);
        return true;
    }
}
