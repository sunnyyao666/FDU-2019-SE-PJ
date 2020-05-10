package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ApplyConferenceRequest;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.ConferenceNameDuplicatedException;
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
class ConferenceServiceTest extends BackendTest {
    @Autowired
    ConferenceService conferenceService;

    @Test
    @Transactional
    void applyConference() {
        User testChair = addUser("testChair");
        fakeLogin("testChair");
        Conference testConference = addConference(testChair, "fullName");
        conferenceRepository.save(testConference);
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        ApplyConferenceRequest testRequest1 = new ApplyConferenceRequest("abb", "testConferenceFullName", "place", testStartDate, testEndDate, testReleaseDate, testDDLDate, "1");
        assertDoesNotThrow(() -> conferenceService.applyConference(testRequest1));
        ApplyConferenceRequest testRequest2 = new ApplyConferenceRequest("abb", "testConferenceFullName", "place", testStartDate, testEndDate, testReleaseDate, testDDLDate, "1");
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
        User testChair = addUser("testChair");
        addConference(testChair, "testConferenceFullName");
        assertDoesNotThrow(() -> conferenceService.changeSubmissionState("testConferenceFullName", true));
    }

    @Test
    @Transactional
    void auditConferenceApplication() {
        User testChair = addUser("testChair");
        addConference(testChair, "testConferenceFullName");
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("testConferenceFullName", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("testConferenceFullName", true));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", false));
        assertDoesNotThrow(() -> conferenceService.auditConferenceApplication("nameThatNoExist", true));
    }

    @Test
    @Transactional
    void searchConference() {
        User testChair = addUser("testChair");
        addConference(testChair, "testConferenceFullName");
        assertNotNull(conferenceService.searchConference("testConferenceFullName"));
    }

}