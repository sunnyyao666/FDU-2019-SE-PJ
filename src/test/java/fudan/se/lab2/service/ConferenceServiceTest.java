package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ApplyConferenceRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.ConferenceNameDuplicatedException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConferenceServiceTest {
    @Autowired
    ConferenceService conferenceService;
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
    void applyConference() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        fakeLogin("testChair");
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        authorityRepository.save(new Authority("Chair", testChair, "fullName", null));

        ApplyConferenceRequest testRequest1 = new ApplyConferenceRequest("testConferenceFullName", "abb", "place", testStartDate, testEndDate, testDDLDate, testReleaseDate);
        assertDoesNotThrow(() -> conferenceService.applyConference(testRequest1));
        ApplyConferenceRequest testRequest2 = new ApplyConferenceRequest("fullName", "abb", "place", testStartDate, testEndDate, testDDLDate, testReleaseDate);
        assertThrows(ConferenceNameDuplicatedException.class, () -> conferenceService.applyConference(testRequest2));
    }

    @Test
    @Transactional
    void invitePCMember() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        authorityRepository.save(new Authority("Chair", testChair, "fullName", null));
        System.out.println(userRepository.findByUsername("testChair").getConferences());

        User user = new User("testUser", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin("testChair");
        assertDoesNotThrow(() -> conferenceService.invitePCMember("testUser", "fullName"));
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
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        authorityRepository.save(new Authority("Chair", testChair, "fullName", null));
        User user = new User("testUser", encoder.encode(password), "testUserFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        assertNotNull(conferenceService.searchUsers("User", "testConferenceFullName"));

        fakeLogin("testChair");
        conferenceService.invitePCMember("testUser", "testConferenceFullName");
        assertTrue(conferenceService.searchUsers("testUser", "testConferenceFullName").isEmpty());
    }

    @Test
    @Transactional
    void listConferences() {
        fakeLogin();

        assertDoesNotThrow(() -> conferenceService.listConferences("user"));
        assertDoesNotThrow(() -> conferenceService.listConferences("contribution"));
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void auditPCInvitationApplication(boolean bool) {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testChairFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        User user = new User("testUserName", encoder.encode(password), "testUserFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        authorityRepository.save(new Authority("Undetermined PC Member", user, "testConferenceFullName", "testChair"));
        //数据库中添加测试用权限、用户、会议。

        fakeLogin();
        assertDoesNotThrow(() -> conferenceService.auditPCInvitationApplication("testConferenceFullName", bool));
    }

    @Test
    @Transactional
    void changeSubmissionState() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);

        assertDoesNotThrow(() -> conferenceService.changeSubmissionState("fullName", true));
    }

    @Test
    @Transactional
    void auditConferenceApplication() {
        User testChair = new User("testChair", encoder.encode("111111a"), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);

        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("fullName", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("fullName", true));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", true));
    }

    @Test
    @Transactional
    void listInviteHistory() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        //添加会议

        User user1 = new User("testUser1", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        User user2 = new User("testUser2", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user1);
        userRepository.save(user2);
        fakeLogin("testChair");
        conferenceService.invitePCMember("testUser1", "fullName");
        conferenceService.invitePCMember("testUser2", "fullName");
        assertNotNull(conferenceService.listInviteHistory("fullName"));
    }

    @Test
    @Transactional
    void searchConference() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        assertNotNull(conferenceService.searchConference("fullName"));
    }

    @Test
    @Transactional
    void submitThesis() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "fullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "fullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        //添加会议
        User user = new User("testUser", encoder.encode("111111a"), "fullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin("testUser");
        File file = new File("src/test/java/fudan/se/lab2/service/test.pdf");
        MultipartFile testFile = null;
        try {
            testFile = new MockMultipartFile("test.pdf", "test.pdf", "pdf", new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartFile finalTestFile = testFile;
        assertDoesNotThrow(() -> conferenceService.submitThesis("fullName", "title", "summary", finalTestFile) );
    }

    private void fakeLogin() {
        fakeLogin("testUserName");
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