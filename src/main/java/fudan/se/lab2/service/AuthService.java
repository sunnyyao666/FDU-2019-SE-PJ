package fudan.se.lab2.service;

import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.WrongPasswordException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PasswordEncoder encoder;

    @Autowired
    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.encoder = encoder;
    }

    public User register(RegisterRequest request) throws UsernameHasBeenRegisteredException {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) throw new UsernameHasBeenRegisteredException(username);
        user = new User(username, encoder.encode(request.getPassword()), request.getFullname(), new HashSet<>(Collections.singletonList(authorityRepository.findByAuthority("User"))));
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) throws UsernameNotFoundException,WrongPasswordException{
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User: '" + username + "' not found.");
        if (!encoder.matches(password, user.getPassword())) throw new WrongPasswordException(username);
        return user;
    }


}
