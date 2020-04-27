package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;
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
        User user = new User("testUsername", encoder.encode(password), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        assertThrows(UsernameNotFoundException.class, () -> authService.login("testUsername1", password));
        assertNotNull(authService.login("testUsername", password));
        assertThrows(BadCredentialsException.class, () -> authService.login("testUsername", "111112a"));
    }

    @Test
    @Transactional
    void register() {
        String password = "111111a";
        User user = new User("testUsername", encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(user);
        RegisterRequest testRegister1 = new RegisterRequest("testUsername", "password", "testFullName", "testEmail@t.com", "off", new String[0]);
        RegisterRequest testRegister2 = new RegisterRequest("testUsername1", "password", "testFullName", "testEmail@t.com", "off", new String[0]);
        assertThrows(UsernameHasBeenRegisteredException.class, () -> authService.register(testRegister1));
        assertNotNull(authService.register(testRegister2));
    }

    @Test
    @Transactional
    void updateUser() {
        User user = new User("testUsername", encoder.encode("111111a"), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin();
        assertNotNull(authService.updateUser());
    }

    @Test
    @Transactional
    void invitePCMember() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, "1",testChair);
        conferenceRepository.save(testConference);
        authorityRepository.save(new Authority("Chair", testChair, "testConferenceFullName", null));
        System.out.println(userRepository.findByUsername("testChair").getConferences());

        User user = new User("testUsername", encoder.encode("111111a"), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin("testChair");
        assertDoesNotThrow(() -> authService.invitePCMember("testUsername", "testConferenceFullName"));
    }

    @Test
    @Transactional
    void searchUsers() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testChairFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, "1",testChair);
        conferenceRepository.save(testConference);
        authorityRepository.save(new Authority("Chair", testChair, "testConferenceFullName", null));
        User user = new User("testUsername", encoder.encode(password), "testUserFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        assertNotNull(authService.searchUsers("User", "testConferenceFullName"));

        fakeLogin("testChair");
        authService.invitePCMember("testUsername", "testConferenceFullName");
        assertTrue(authService.searchUsers("testUsername", "testConferenceFullName").isEmpty());
    }

    @Test
    @Transactional
    void auditPCInvitationApplication() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testChairFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate,"1", testChair);
        conferenceRepository.save(testConference);
        User user = new User("testUsername", encoder.encode(password), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        authorityRepository.save(new Authority("Undetermined PC Member", user, "testConferenceFullName", "testChair"));
        //数据库中添加测试用权限、用户、会议。

        fakeLogin();
        assertDoesNotThrow(() -> authService.auditPCInvitationApplication("testConferenceFullName", "1"));

        authorityRepository.save(new Authority("Undetermined PC Member", user, "testConferenceFullName", "testChair"));
        assertDoesNotThrow(() -> authService.auditPCInvitationApplication("testConferenceFullName", "false"));
    }

    @Test
    @Transactional
    void listInviteHistory() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate,"1", testChair);
        conferenceRepository.save(testConference);
        //添加会议

        User user1 = new User("testUsername", encoder.encode("111111a"), "testFullName", "testEmail@t.com", "off", new String[0]);
        User user2 = new User("testUsername1", encoder.encode("111111a"), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user1);
        userRepository.save(user2);
        fakeLogin("testChair");
        authService.invitePCMember("testUsername", "testConferenceFullName");
        authService.invitePCMember("testUsername1", "testConferenceFullName");
        assertNotNull(authService.listInviteHistory("testConferenceFullName"));
    }


    private void fakeLogin() {
        fakeLogin("testUsername");
    }

    private void fakeLogin(String username) {
        //伪造登录信息
        User userDetails = new User();
        userDetails.setFullName("testFullName");
        userDetails.setUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
