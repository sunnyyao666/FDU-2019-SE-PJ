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
        this.setUserRepository(userRepository);
        this.setAuthorityRepository(authorityRepository);
        this.setConferenceRepository(conferenceRepository);
        this.setEncoder(encoder);
    }

    public Conference conferenceApply(ConferenceApplyRequest request) throws BadCredentialsException, ConferenceNameDuplicatedExecption {
        String fullName = request.getFullName();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        Conference conference = getConferenceRepository().findByFullName(request.getFullName());
        if (conference != null) throw new ConferenceNameDuplicatedExecption(request.getFullName());
        conference = new Conference(request.getAbbreviation(), fullName, request.getPlace(), request.getStartDate(), request.getEndDate(), request.getDeadline(), request.getReleaseTime(), getUserRepository().findByUsername(userDetails.getUsername()));
        getConferenceRepository().save(conference);
        return conference;
    }

    public User register(RegisterRequest request) throws UsernameHasBeenRegisteredException {
        String username = request.getUsername();
        User user = getUserRepository().findByUsername(username);
        if (user != null) throw new UsernameHasBeenRegisteredException(username);
        user = new User(username, getEncoder().encode(request.getPassword()), request.getFullName(), request.getEmail(), request.getOffice(), null);
        getUserRepository().save(user);
        return user;
    }

    public User login(String username, String password) throws UsernameNotFoundException, BadCredentialsException {
        User user = getUserRepository().findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User: '" + username + "' not found.");
        if (!getEncoder().matches(password, user.getPassword()))
            throw new BadCredentialsException("User: '" + username + "' got wrong password.");
        return user;
    }

    public UserRepository getUserRepository()
    {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public AuthorityRepository getAuthorityRepository()
    {
        return authorityRepository;
    }

    public void setAuthorityRepository(AuthorityRepository authorityRepository)
    {
        this.authorityRepository = authorityRepository;
    }

    public ConferenceRepository getConferenceRepository()
    {
        return conferenceRepository;
    }

    public void setConferenceRepository(ConferenceRepository conferenceRepository)
    {
        this.conferenceRepository = conferenceRepository;
    }

    public PasswordEncoder getEncoder()
    {
        return encoder;
    }

    public void setEncoder(PasswordEncoder encoder)
    {
        this.encoder = encoder;
    }
}
