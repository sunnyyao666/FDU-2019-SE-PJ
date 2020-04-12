package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService AuthService;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    PasswordEncoder encoder;

    @Test
    @Transactional
    void login() {
        String password = "111111a";
        User user = new User("testUser", encoder.encode(password), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        assertThrows(UsernameNotFoundException.class, () -> AuthService.login("testUser1", password));
        assertNotNull(AuthService.login("testUser", password));
        assertThrows(BadCredentialsException.class, () -> AuthService.login("testUser", "111112a"));
    }

    @Test
    @Transactional
    void register() {
        String password = "111111a";
        User user = new User("testUser", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(user);
        RegisterRequest testRegister1 = new RegisterRequest("testUser", "password", "fullName", "testEmail@t.com", "off", new String[0]);
        RegisterRequest testRegister2 = new RegisterRequest("testUser1", "password", "fullName", "testEmail@t.com", "off", new String[0]);
        assertThrows(UsernameHasBeenRegisteredException.class, () -> AuthService.register(testRegister1));
        assertNotNull(AuthService.register(testRegister2));
    }

    @Test
    @Transactional
    void updateUser() {
        User user = new User("testUser", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin();
        assertNotNull(AuthService.updateUser());
    }

    private void fakeLogin() {
        //伪造登录信息
        User userDetails = new User();
        userDetails.setFullName("testFullName");
        userDetails.setUsername("testUser");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
