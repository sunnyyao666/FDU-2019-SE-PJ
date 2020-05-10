package fudan.se.lab2.service;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BackendTest {
    @Autowired
    ThesisRepository thesisRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    PCAuditRepository pcAuditRepository;
    @Autowired
    PasswordEncoder encoder;

    void fakeLogin() {
        fakeLogin("testUsername");
    }

    void fakeLogin(String username) {
        //伪造登录信息
        User userDetails = new User();
        userDetails.setFullName("testFullName");
        userDetails.setUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    User addUser(String username) {
        String password = "111111a";
        User user = new User(username, encoder.encode(password), "testFullName", "323@d.d", "off", new String[0]);
        userRepository.save(user);
        return user;
    }

    Conference addConference(User chair, String conferenceFullName) {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        Conference testConference = new Conference("ABB", conferenceFullName, "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, "1", chair);
        conferenceRepository.save(testConference);
        return testConference;
    }

    Conference addConference(User chair, String ConferenceFullName, String topics) {
        Date testStartDate = new Date(10);
        Date testEndDate = new Date(11);
        Date testReleaseDate = new Date(2);
        Date testDDLDate = new Date(2);
        Conference testConference = new Conference("ABB", ConferenceFullName, "Place", testStartDate, testEndDate, testReleaseDate, testDDLDate, topics, chair);
        conferenceRepository.save(testConference);
        return testConference;
    }

    Thesis addThesis(String conferenceFullName, String title, String summary, User user, String authors, String topics, String fileName, String thesisPath) {
        Thesis thesis = new Thesis(conferenceFullName, title, summary, user, authors, topics, fileName, thesisPath);
        thesisRepository.save(thesis);
        return thesis;
    }
}
