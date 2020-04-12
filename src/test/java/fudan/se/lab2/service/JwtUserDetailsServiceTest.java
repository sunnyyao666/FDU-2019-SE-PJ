package fudan.se.lab2.service;

import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JwtUserDetailsServiceTest {
    @Autowired
    JwtUserDetailsService JwtUserDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    PasswordEncoder encoder;

    @Test
    @Transactional
    void loadUserByUsername() {
        User user = new User("testUser", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        assertNotNull(JwtUserDetailsService.loadUserByUsername("testUser"));
    }
}