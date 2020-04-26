package fudan.se.lab2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ThesisRepository;
import fudan.se.lab2.repository.UserRepository;
import net.sf.json.JSONArray;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author YHT
 */
@Service
public class ThesisService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private ThesisRepository thesisRepository;

    @Autowired
    public ThesisService(UserRepository userRepository, AuthorityRepository authorityRepository, ThesisRepository thesisRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.thesisRepository = thesisRepository;
    }

    public Thesis submitThesis(Long id, String conferenceFullName, String title, String summary, String authors, String topics, MultipartFile file) throws BadCredentialsException {
//        JSONArray jsonWriters = JSONArray.fromObject(authors);
//        Set<User> authors = new HashSet<>();
//        for (int i = 0; i < jsonWriters.size(); i++) {
//
//            JSONObject jsonObject = jsonWriters.getJSONObject(i);
//
//            String fullName = jsonObject.get("fullName").toString();
//            String email = jsonObject.get("email").toString();
//            String office = jsonObject.get("office").toString();
//            Object regionO = jsonObject.get("region");
//            ObjectMapper objectMapper = new ObjectMapper();
//            String[] region = new String[3];
//            region = objectMapper.convertValue(regionO, region.getClass());
//            User author = new User(fullName, email, office, region);
//
//            authors.add(author);
//        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        File target = new File(System.getProperty("user.dir"), "/static/" + conferenceFullName + "/" + user.getUsername() + "/");
        if (!target.exists()) target.mkdirs();
        StringBuilder path = new StringBuilder(target.getAbsolutePath() + "/" + title);
        while (new File(path + ".pdf").exists()) path.append("(1)");
        String thesisPath = path + ".pdf";
        if (id == -1) {
            try (FileOutputStream out = new FileOutputStream(thesisPath)) {
                out.write(file.getBytes());
                out.flush();
            } catch (IOException ex) {
                throw new BadCredentialsException("Bad uploading!");
            }
            Thesis thesis = new Thesis(conferenceFullName, title, summary, user, authors, topics, thesisPath);
            thesisRepository.save(thesis);
            if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Author", user, conferenceFullName).isEmpty())
                authorityRepository.save(new Authority("Author", user, conferenceFullName, null));
            return thesis;
        } else {
            Thesis thesis = thesisRepository.findById(id).get();
            thesis.setTitle(title);
            thesis.setSummary(summary);
            thesis.setAuthors(authors);
            thesis.setTopics(topics);
            if (!file.isEmpty()) {
                try (FileOutputStream out = new FileOutputStream(thesisPath)) {
                    out.write(file.getBytes());
                    out.flush();
                } catch (IOException ex) {
                    throw new BadCredentialsException("Bad uploading!");
                }
                thesis.setPath(thesisPath);
            }
            thesisRepository.save(thesis);
            return thesis;
        }
    }
}
