package fudan.se.lab2.service;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.exception.ConferenceNameDuplicatedExecption;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.controller.request.ConferenceApplyRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author YHT
 */
@Service
public class AuthService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private ConferenceRepository conferenceRepository;
    private PasswordEncoder encoder;

    @Autowired
    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.conferenceRepository = conferenceRepository;
        this.encoder = encoder;
    }

    public Conference conferenceApply(ConferenceApplyRequest request) throws BadCredentialsException, ConferenceNameDuplicatedExecption {
        String fullName = request.getFullName();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        Conference conference = conferenceRepository.findByFullName(request.getFullName());
        if (conference != null) throw new ConferenceNameDuplicatedExecption(request.getFullName());
        conference = new Conference(fullName, (User) userDetails);
        conferenceRepository.save(conference);
        return conference;
    }

    public User register(RegisterRequest request) throws UsernameHasBeenRegisteredException {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) throw new UsernameHasBeenRegisteredException(username);
        user = new User(username, encoder.encode(request.getPassword()), request.getEmail(), request.getOffice(), new HashSet<>(Collections.singletonList(authorityRepository.findByAuthority("User"))));
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


}
