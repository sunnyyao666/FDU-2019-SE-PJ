package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest extends BackendTest {
    @Autowired
    AuthService authService;

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
        addUser("testUser");
        fakeLogin("testUser");
        assertNotNull(authService.updateUser());
    }

    @Test
    @Transactional
    void invitePCMember() {
        User testChair = addUser("testChair");
        addConference(testChair, "testConferenceFullName");
        authorityRepository.save(new Authority("Chair", testChair, "testConferenceFullName", null));
        addUser("testUsername");
        fakeLogin("testChair");

        assertTrue(authService.invitePCMember("testUsername", "testConferenceFullName"));
    }

    @Test
    @Transactional
    void searchUsers() {
        User testChair = addUser("testChair");
        addConference(testChair, "testConferenceFullName");
        authorityRepository.save(new Authority("Chair", testChair, "testConferenceFullName", null));
        addUser("testUsername");

        assertNotNull(authService.searchUsers("User", "testConferenceFullName"));

        fakeLogin("testChair");
        authService.invitePCMember("testUsername", "testConferenceFullName");

        assertTrue(authService.searchUsers("testUsername", "testConferenceFullName").isEmpty());
    }

    @Test
    @Transactional
    void auditPCInvitationApplication() {
        User testChair = addUser("testChair");
        Conference testConference = addConference(testChair, "testConferenceFullName");
        User user = addUser("testUsername");
        authorityRepository.save(new Authority("Undetermined PC Member", user, "testConferenceFullName", "testChair"));
        fakeLogin();

        assertDoesNotThrow(() -> authService.auditPCInvitationApplication("testConferenceFullName", "1"));

        authorityRepository.save(new Authority("Undetermined PC Member", user, "testConferenceFullName", "testChair"));
        assertDoesNotThrow(() -> authService.auditPCInvitationApplication("testConferenceFullName", "false"));
    }

    @Test
    @Transactional
    void listInviteHistory() {
        User chair = addUser("chair");
        addConference(chair, "conferenceFullName");
        addUser("user1");
        addUser("user2");
        fakeLogin("chair");
        authService.invitePCMember("user1", "conferenceFullName");
        authService.invitePCMember("user2", "conferenceFullName");

        assertNotNull(authService.listInviteHistory("conferenceFullName"));
    }
}
