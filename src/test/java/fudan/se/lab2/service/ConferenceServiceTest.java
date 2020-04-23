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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConferenceServiceTest {
    @Autowired
    ConferenceService conferenceService;
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

        ApplyConferenceRequest testRequest1 = new ApplyConferenceRequest("abb", "testConferenceFullName", "place", testStartDate, testEndDate, testReleaseDate, testDDLDate);
        assertDoesNotThrow(() -> conferenceService.applyConference(testRequest1));
        ApplyConferenceRequest testRequest2 = new ApplyConferenceRequest("abb", "testConferenceFullName", "place", testStartDate, testEndDate, testReleaseDate, testDDLDate);
        assertThrows(ConferenceNameDuplicatedException.class, () -> conferenceService.applyConference(testRequest2));
    }

    @Test
    @Transactional
    void listConferences() {
        fakeLogin();

        assertDoesNotThrow(() -> conferenceService.listConferences("user"));
        assertDoesNotThrow(() -> conferenceService.listConferences("contribution"));
    }

    @Test
    @Transactional
    void changeSubmissionState() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testConferenceFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);

        assertDoesNotThrow(() -> conferenceService.changeSubmissionState("testConferenceFullName", true));
    }

    @Test
    @Transactional
    void auditConferenceApplication() {
        User testChair = new User("testChair", encoder.encode("111111a"), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);

        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("testConferenceFullName", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("testConferenceFullName", true));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", true));
    }

    @Test
    @Transactional
    void searchConference() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testChair);
        conferenceRepository.save(testConference);
        assertNotNull(conferenceService.searchConference("testConferenceFullName"));
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