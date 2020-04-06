package fudan.se.lab2.service;

import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    private UserRepository userRepository;
    private PasswordEncoder encoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User register(RegisterRequest request) throws UsernameHasBeenRegisteredException {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) throw new UsernameHasBeenRegisteredException(username);
        user = new User(username, encoder.encode(request.getPassword()), request.getFullName(), request.getEmail(), request.getOffice());
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
