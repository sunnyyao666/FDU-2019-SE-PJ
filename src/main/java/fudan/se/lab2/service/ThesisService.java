package fudan.se.lab2.service;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.ThesisRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public Thesis submitThesis(String conferenceFullName, String title, String summary, MultipartFile file) throws BadCredentialsException {
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
        Thesis thesis = new Thesis(title, user, conferenceFullName, summary, thesisPath);
        thesisRepository.save(thesis);
        if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Author", user, conferenceFullName).isEmpty())
            authorityRepository.save(new Authority("Author", user, conferenceFullName, null));
        return thesis;
    }
}
