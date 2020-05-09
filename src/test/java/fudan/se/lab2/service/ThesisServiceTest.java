package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.AuditThesisRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.*;
import net.sf.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ThesisServiceTest extends BackendTest {
    @Autowired
    ThesisService thesisService;
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

    @Test
    @Transactional
    void submitThesis() {
        User testChair = addUser("testChair");
        Conference testConference = addConference(testChair, "testConferenceFullName");
        User user = addUser("testUsername");
        fakeLogin("testUsername");
        File file = new File("src/test/java/fudan/se/lab2/service/test.pdf");
        MultipartFile testFile = null;
        try {
            testFile = new MockMultipartFile("test.pdf", "test.pdf", "pdf", new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartFile finalTestFile = testFile;

        User[] testUsers = new User[2];
        testUsers[0] = new User("testFullName1", "323@d.d", "off", new String[0]);
        testUsers[1] = new User("testFullName2", "323@d.d", "off", new String[0]);
        JSONArray jsonarray = JSONArray.fromObject(testUsers);

        assertDoesNotThrow(() -> thesisService.submitThesis(-1L, "testConferenceFullName", "title", "summary", jsonarray.toString(), "1", finalTestFile));
    }

    @Test
    @Transactional
    void startAudit1() {
        User testChair = addUser("testChair");
        Conference testConference = addConference(testChair, "testConferenceFullName", "[\"1\",\"2\",\"3\",\"4\",\"5\"]");
        fakeLogin("testChair");
        User users[] = new User[5];
        for (int i = 0; i < 5; i++) users[i] = addUser("user" + i);
        for (int i = 0; i < 5; i++)
            addThesis("testConferenceFullName", "title", "summary", users[i], "", "['" + i + "']", String.valueOf(i), "");
        User PCMembers[] = new User[3];
        for (int i = 0; i < 3; i++) {
            PCMembers[i] = addUser("PC" + i);
            Authority authority = new Authority("PC Member", PCMembers[i], "testConferenceFullName", "testChair");
            authority.setTopics("['" + i + "']");
            authorityRepository.save(authority);
        }

        assert (thesisService.startAudit1("testConferenceFullName").equals("OK"));
    }

    @Test
    @Transactional
    void testAudit() {
        String conferenceFullName = "testConferenceFullName";
        User testChair = addUser("testChair");
        Conference testConference = addConference(testChair, conferenceFullName, "[\"1\",\"2\",\"3\",\"4\",\"5\"]");
        fakeLogin("testChair");
        User users[] = new User[5];
        for (int i = 0; i < 5; i++) users[i] = addUser("user" + i);
        for (int i = 0; i < 5; i++)
            addThesis(conferenceFullName, "title", "summary", users[i], "", "[\"" + i + "\",\"" + ((i + 1) % 5) + "\"]", String.valueOf(i), "");
        User PCMembers[] = new User[6];
        for (int i = 0; i < 6; i++) {
            PCMembers[i] = addUser("PC" + i);
            Authority authority = new Authority("PC Member", PCMembers[i], conferenceFullName, "testChair");
            authority.setTopics("[\"" + i + "\"]");
            authorityRepository.save(authority);
        }

        assertEquals(thesisService.startAudit2(conferenceFullName), "OK");

        for (int i = 0; i < 6; i++) {
            fakeLogin(PCMembers[i].getUsername());
            Set<Thesis> theses = thesisService.pcGetTheses(conferenceFullName);
            System.out.println(i + " " + theses.size());
            for (Thesis thesis : theses) {
                AuditThesisRequest request = new AuditThesisRequest();
                request.setConferenceFullName(conferenceFullName);
                request.setComment("comment");
                request.setConfidence("1");
                request.setScore(1);
                request.setThesisID(thesis.getId());

                assertDoesNotThrow(() -> thesisService.auditThesis(request));
            }
        }

        assertTrue(thesisService.endAudit(conferenceFullName));
    }
}