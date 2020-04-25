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


    public Object testJSON(String writers){
        JSONArray json = JSONArray.fromObject(writers);
        return  json;
    }


    public Object submitThesis(String conferenceFullName, String title, String summary, MultipartFile file,String authorsStr) throws BadCredentialsException {
        JSONArray jsonWriters = JSONArray.fromObject(authorsStr);
        Set<User> authors=new HashSet<>();
        for(int i=0;i<jsonWriters.size();i++) {

            JSONObject jo = JSONObject.fromObject(jsonWriters.get(i));

            String fullName= jsonWriters.getJSONObject(i).get("fullName").toString();

            String email=(String) jsonWriters.getJSONObject(i).get("email").toString();
            String office=(String) jsonWriters.getJSONObject(i).get("office").toString();
            Object regionO= jsonWriters.getJSONObject(i).get("region");
            ObjectMapper objectMapper = new ObjectMapper();
            String[] region=new String[3];
            region=objectMapper.convertValue(regionO,region.getClass());
            User author=new User(fullName,email,office,region);

            authors.add(author);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        File target = new File(System.getProperty("user.dir"), "/static/" + conferenceFullName + "/" + user.getUsername() + "/");
        if (!target.exists()) target.mkdirs();
        StringBuilder path = new StringBuilder(target.getAbsolutePath() + "/" + title);
        while (new File(path + ".pdf").exists()) path.append("(1)");
        String thesisPath = path + ".pdf";
        try (FileOutputStream out = new FileOutputStream(thesisPath)) {
            out.write(file.getBytes());
            out.flush();
        } catch (IOException ex) {
            throw new BadCredentialsException("Bad uploading!");
        }
        Thesis thesis = new Thesis(title, user, conferenceFullName, summary, thesisPath,authorsStr);
        thesisRepository.save(thesis);
        if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Author", user, conferenceFullName).isEmpty())
            authorityRepository.save(new Authority("Author", user, conferenceFullName, null));
        return thesis;
    }
}
