package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.AuditThesisRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import net.sf.json.JSONArray;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author YHT
 */
@Service
public class ThesisService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ConferenceRepository conferenceRepository;
    private final ThesisRepository thesisRepository;
    private final PCAuditRepository pcAuditRepository;
    private final PostRepository postRepository;

    @Autowired
    public ThesisService(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, ThesisRepository thesisRepository, PCAuditRepository pcAuditRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.conferenceRepository = conferenceRepository;
        this.thesisRepository = thesisRepository;
        this.pcAuditRepository = pcAuditRepository;
        this.postRepository = postRepository;
    }

    public Thesis submitThesis(Long id, String conferenceFullName, String title, String summary, String authors, String topics, MultipartFile file) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        File target = new File(new File(new File(System.getProperty("user.dir"), "static"), conferenceFullName), user.getUsername());
        if (!target.exists()) target.mkdirs();
        StringBuilder realTitle = new StringBuilder(title);
        while (new File(target.getAbsolutePath(), realTitle.toString() + ".pdf").exists()) realTitle.append("(1)");
        String thesisPath = new File(target.getAbsolutePath(), realTitle.toString() + ".pdf").getAbsolutePath();
        if (id == -1) {
            String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
            try (FileOutputStream out = new FileOutputStream(thesisPath)) {
                out.write(file.getBytes());
                out.flush();
            } catch (IOException ex) {
                throw new BadCredentialsException("Bad uploading!");
            }
            Thesis thesis = new Thesis(conferenceFullName, title, summary, user, authors, topics, fileName, thesisPath);
            thesisRepository.save(thesis);
            if (authorityRepository.findAllByAuthorityContainingAndUserAndConferenceFullName("Author", user, conferenceFullName).isEmpty())
                authorityRepository.save(new Authority("Author", user, conferenceFullName, null));
            return thesis;
        }
        Thesis thesis;
        Optional<Thesis> optionalThesis = thesisRepository.findById(id);
        if (optionalThesis.isPresent()) thesis = optionalThesis.get();
        else throw new BadCredentialsException("No such thesis!");
        thesis.setTitle(title);
        thesis.setSummary(summary);
        thesis.setAuthors(authors);
        thesis.setTopics(topics);
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
            thesis.setFileName(fileName);
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

    public String startAudit1(String conferenceFullName) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        if (theses.isEmpty()) return "No thesis";
        for (Thesis thesis : theses) {
            List<Authority> pcMembers = new ArrayList<>();
            List<String> topics = (List<String>) JSONArray.toList(JSONArray.fromObject(thesis.getTopics()), String.class, new JsonConfig());
            for (String topic : topics)
                pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullNameAndTopicsContaining("PC Member", conferenceFullName, topic));
            if (pcMembers.size() < 2)
                pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
            pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
            Collections.sort(pcMembers);
            if (corresponding(thesis, pcMembers) == 3) continue;
            if (pcMembers.size() == authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName).size()) {
                pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
                return "Fail to distribute";
            }
            pcAuditRepository.deleteAllByThesisID(thesis.getId());
            pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
            pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
            Collections.sort(pcMembers);
            if (corresponding(thesis, pcMembers) < 3) {
                pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
                return "Fail to distribute";
            }
        }
        conference.setAuditing(true);
        conference.setSubmitting(false);
        conferenceRepository.save(conference);
        return "OK";
    }

    public String startAudit2(String conferenceFullName) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        List<Thesis> theses = new ArrayList<>(thesisRepository.findAllByConferenceFullName(conferenceFullName));
        if (theses.isEmpty()) return "No thesis";
        int n = 0;
        while (n <= 10) {
            for (Thesis thesis : theses) {
                List<Authority> pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
                pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
                Collections.shuffle(pcMembers);
                Collections.sort(pcMembers);
                if (corresponding(thesis, pcMembers) < 3) {
                    pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
                    return "Fail to distribute";
                }
            }
            List<Authority> pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
            pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
            Collections.sort(pcMembers);
            int min = pcMembers.iterator().next().getPCAudits().size();
            Collections.reverse(pcMembers);
            int max = pcMembers.iterator().next().getPCAudits().size();
            if (max - min <= 1) break;
            else {
                pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
                Collections.shuffle(theses);
                n++;
            }
        }
        if (n > 10) return "Fail to distribute";
        conference.setAuditing(true);
        conference.setSubmitting(false);
        conferenceRepository.save(conference);
        return "OK";
    }

    private int corresponding(Thesis thesis, List<Authority> pcMembers) {
        JSONArray jsonAuthors = JSONArray.fromObject(thesis.getAuthors());
        Map<String, String> authors = new HashMap<>();
        for (int i = 0; i < jsonAuthors.size(); i++) {
            JSONObject jsonObject = jsonAuthors.getJSONObject(i);
            authors.put(jsonObject.get("fullName").toString(), jsonObject.get("email").toString());
        }
        int n = 0;
        for (Authority pcMember : pcMembers) {
            User user = pcMember.getUser();
            if (user.getUsername().equals(thesis.getSubmitter().getUsername())) continue;
            boolean isAuthor = false;
            for (Map.Entry<String, String> entry : authors.entrySet())
                if ((user.getFullName().equals(entry.getKey())) && (user.getEmail().equals(entry.getValue()))) {
                    isAuthor = true;
                    break;
                }
            if (isAuthor) continue;
            PCAudit pcAudit = new PCAudit(pcMember, thesis);
            pcAuditRepository.save(pcAudit);
            pcMember.getPCAudits().add(pcAudit);
            n++;
            if (n == 3) return 3;
        }
        return n;
    }

    public Set<Thesis> pcGetTheses(String conferenceFullName) throws BadCredentialsException {
        Set<Thesis> theses = new HashSet<>();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Authority authority = authorityRepository.findByAuthorityAndUserAndConferenceFullName("PC Member", user, conferenceFullName);
        if (authority == null)
            authority = authorityRepository.findByAuthorityAndUserAndConferenceFullName("Chair", user, conferenceFullName);
        if (authority == null) throw new BadCredentialsException("Not authorized.");
        for (PCAudit pcAudit : authority.getPCAudits()) theses.add(pcAudit.getThesis());
        return theses;
    }

    public PCAudit auditThesis(AuditThesisRequest request) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Authority authority = authorityRepository.findByAuthorityAndUserAndConferenceFullName("PC Member", user, request.getConferenceFullName());
        if (authority == null)
            authority = authorityRepository.findByAuthorityAndUserAndConferenceFullName("Chair", user, request.getConferenceFullName());
        if (authority == null) throw new BadCredentialsException("Not authorized.");
        PCAudit pcAudit = pcAuditRepository.findByAuthorityAndThesisID(authority, request.getThesisID());
        if (pcAudit == null) throw new BadCredentialsException("Bad Operation.");
        pcAudit.setScore(request.getScore());
        pcAudit.setComment(request.getComment());
        pcAudit.setConfidence(request.getConfidence());
        pcAudit.setAudited(true);
        if (request.getStage() == 1) pcAudit.setRechanged1(true);
        else if (request.getStage() == 2) pcAudit.setRechanged2(true);
        pcAuditRepository.save(pcAudit);
        return pcAudit;
    }

    public boolean endAudit(String conferenceFullName) {
        Set<PCAudit> pcAudits = pcAuditRepository.findAllByAuthority_ConferenceFullName(conferenceFullName);
        for (PCAudit pcAudit : pcAudits) if (!pcAudit.isAudited()) return false;
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        for (Thesis thesis : theses) {
            thesis.setAudited(true);
            thesisRepository.save(thesis);
        }
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        conference.setSubmitting(true);
        conference.setRechanging1(true);
        conferenceRepository.save(conference);
        return true;
    }

    public void downloadThesis(Long id, HttpServletResponse response) throws BadCredentialsException {
        Thesis thesis;
        Optional<Thesis> optionalThesis = thesisRepository.findById(id);
        if (optionalThesis.isPresent()) thesis = optionalThesis.get();
        else throw new BadCredentialsException("No such thesis!");
        try (InputStream inputStream = new FileInputStream(new File(thesis.getPath()));
             OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + thesis.getFileName());
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad downloading!");
        }
    }

    public Post post(Long thesisID, String text) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Thesis thesis;
        Optional<Thesis> optionalThesis = thesisRepository.findById(thesisID);
        if (optionalThesis.isPresent()) thesis = optionalThesis.get();
        else throw new BadCredentialsException("No such thesis!");
        Post post = new Post(thesis, user.getUsername(), text);
        postRepository.save(post);
        return post;
    }

    public boolean releaseAcceptance1(String conferenceFullName) {
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        boolean rechanged1 = true;
        for (Thesis thesis : theses) {
            Set<PCAudit> pcAudits = thesis.getPcAudits();
            boolean accepted = true;
            for (PCAudit pcAudit : pcAudits)
                if (!pcAudit.isRechanged1()) {
                    rechanged1 = false;
                    break;
                } else if (pcAudit.getScore() <= 2) accepted = false;
            if (!rechanged1) break;
            thesis.setAccepted(accepted);
            thesisRepository.save(thesis);
            if (accepted) for (PCAudit pcAudit : pcAudits) {
                pcAudit.setRechanged2(true);
                pcAuditRepository.save(pcAudit);
            }
            Conference conference = conferenceRepository.findByFullName(conferenceFullName);
            conference.setRechanging1(false);
            conference.setRebutting(true);
            conferenceRepository.save(conference);
        }
        return rechanged1;
    }

    public boolean endRebut(String conferenceFullName) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        conference.setRebutting(false);
        conference.setRechanging2(true);
        conferenceRepository.save(conference);
        return true;
    }

    public boolean releaseAcceptance2(String conferenceFullName) {
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        boolean rechanged2 = true;
        for (Thesis thesis : theses) {
            Set<PCAudit> pcAudits = thesis.getPcAudits();
            boolean accepted = true;
            for (PCAudit pcAudit : pcAudits)
                if (!pcAudit.isRechanged2()) {
                    rechanged2 = false;
                    break;
                } else if (pcAudit.getScore() <= 2) accepted = false;
            if (!rechanged2) break;
            thesis.setAccepted(accepted);
            thesisRepository.save(thesis);
            Conference conference = conferenceRepository.findByFullName(conferenceFullName);
            conference.setRechanging1(true);
            conferenceRepository.save(conference);
        }
        return rechanged2;
    }

    public Thesis rebut(Long thesisID, String text) throws BadCredentialsException {
        Thesis thesis;
        Optional<Thesis> optionalThesis = thesisRepository.findById(thesisID);
        if (optionalThesis.isPresent()) thesis = optionalThesis.get();
        else throw new BadCredentialsException("No such thesis!");
        thesis.setRebuttal(text);
        thesisRepository.save(thesis);
        return thesis;
    }

    public Thesis getThesis(Long thesisID) throws BadCredentialsException {
        Thesis thesis;
        Optional<Thesis> optionalThesis = thesisRepository.findById(thesisID);
        if (optionalThesis.isPresent()) thesis = optionalThesis.get();
        else throw new BadCredentialsException("No such thesis!");
        return thesis;
    }

    public Set<Thesis> getAllTheses(String conferenceName) {
        return thesisRepository.findAllByConferenceFullName(conferenceName);
    }
}
