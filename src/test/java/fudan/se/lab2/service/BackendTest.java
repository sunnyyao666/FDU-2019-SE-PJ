package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.ThesisRepository;
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
class BackendTest {
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    ThesisRepository thesisRepository;
    @Autowired
    PasswordEncoder encoder;

    protected void fakeLogin() {
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
