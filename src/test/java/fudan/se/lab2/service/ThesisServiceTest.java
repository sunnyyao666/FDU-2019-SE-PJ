package fudan.se.lab2.service;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
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
class ThesisServiceTest {
    @Autowired
    ThesisService thesisService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    PasswordEncoder encoder;

    @Test
    @Transactional
    void submitThesis() {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        String[] testTopic = {"1"};
        String password = "111111a";
        User testChair = new User("testChair", encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(testChair);
        Conference testConference = new Conference("ABB", "testConferenceFullName", "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, testTopic, testChair);
        conferenceRepository.save(testConference);
        //添加会议
        User user = new User("testUsername", encoder.encode("111111a"), "testFullName", "testEmail@t.com", "off", new String[0]);
        userRepository.save(user);
        fakeLogin("testUsername");
        File file = new File("src/test/java/fudan/se/lab2/service/test.pdf");
        MultipartFile testFile = null;
        try {
            testFile = new MockMultipartFile("test.pdf", "test.pdf", "pdf", new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartFile finalTestFile = testFile;
        assertDoesNotThrow(() -> thesisService.submitThesis("testConferenceFullName", "title", "summary", finalTestFile));
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