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
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private ConferenceRepository conferenceRepository;
    private ThesisRepository thesisRepository;
    private PCAuditRepository pcAuditRepository;

    @Autowired
    public ThesisService(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, ThesisRepository thesisRepository, PCAuditRepository pcAuditRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.conferenceRepository = conferenceRepository;
        this.thesisRepository = thesisRepository;
        this.pcAuditRepository = pcAuditRepository;
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
        } else {
            Thesis thesis = thesisRepository.findById(id).get();
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
    }

    public String startAudit1(String conferenceFullName) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        if (theses.isEmpty()) return "No thesis";
        for (Thesis thesis : theses) {
            List<Authority> pcMembers = new ArrayList<>();
            List<String> topics = (List<String>) JSONArray.toList(JSONArray.fromObject(thesis.getTopics()), String.class, new JsonConfig());
            if (topics.size() < 2)
                pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
            else
                for (String topic : topics)
                    pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullNameAndTopicsContaining("PC Member", conferenceFullName, topic));
            pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
            Collections.sort(pcMembers);
            if (corresponding(thesis, pcMembers) < 3) {
//                if (pcMembers.size() < authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName).size()) {
//                    pcAuditRepository.deleteAllByThesisID(thesis.getId());
//                    pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
//                    pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
//                    Collections.sort(pcMembers);
//                    if (corresponding(thesis, pcMembers) < 3) {
//                        pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
//                        return "Fail to distribute";
//                    }
//                } else {
                pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
                return "Fail to distribute";
//                }
            }
        }
        conference.setAuditing(true);
        conference.setSubmitting(false);
        conferenceRepository.save(conference);
        return "OK";
    }

    public String startAudit2(String conferenceFullName) {
        Conference conference = conferenceRepository.findByFullName(conferenceFullName);
        Set<Thesis> theses = thesisRepository.findAllByConferenceFullName(conferenceFullName);
        if (theses.isEmpty()) return "No thesis";
        for (Thesis thesis : theses) {
            List<Authority> pcMembers = new ArrayList<>(authorityRepository.findAllByAuthorityAndConferenceFullName("PC Member", conferenceFullName));
            pcMembers.addAll(authorityRepository.findAllByAuthorityAndConferenceFullName("Chair", conferenceFullName));
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
        if (max - min > 1) {
            pcAuditRepository.deleteAllByAuthority_ConferenceFullName(conferenceFullName);
            return "Fail to distribute";
        }
        conference.setAuditing(true);
        conference.setSubmitting(false);
        conferenceRepository.save(conference);
        return "OK";
    }

    private int corresponding(Thesis thesis, List<Authority> pcMembers) {
//        JSONArray jsonAuthors = JSONArray.fromObject(thesis.getAuthors());
//        Map<String, String> authors = new HashMap<>();
//        for (int i = 0; i < jsonAuthors.size(); i++) {
//            JSONObject jsonObject = jsonAuthors.getJSONObject(i);
//            authors.put(jsonObject.get("fullName").toString(), jsonObject.get("email").toString());
//        }
        int n = 0;
        for (Authority pcMember : pcMembers) {
//            User user = pcMember.getUser();
//            boolean isAuthor = false;
//            for (Map.Entry<String, String> entry : authors.entrySet())
//                if ((user.getFullName().equals(entry.getKey())) && (user.getEmail().equals(entry.getValue()))) {
//                    isAuthor = true;
//                    break;
//                }
//            if (isAuthor) continue;
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
        if (authority == null) throw new BadCredentialsException("Not authorized.");
        for (PCAudit pcAudit : authority.getPCAudits()) theses.add(pcAudit.getThesis());
        return theses;
    }

    public PCAudit auditThesis(AuditThesisRequest request) throws BadCredentialsException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new BadCredentialsException("Not authorized.");
        User user = userRepository.findByUsername(userDetails.getUsername());
        Authority authority = authorityRepository.findByAuthorityAndUserAndConferenceFullName("PC Member", user, request.getConferenceFullName());
        if (authority == null) throw new BadCredentialsException("Not authorized.");
        PCAudit pcAudit = pcAuditRepository.findByAuthorityAndThesisID(authority, request.getThesisID());
        if (pcAudit == null) throw new BadCredentialsException("Bad Operation.");
        pcAudit.setScore(request.getScore());
        pcAudit.setComment(request.getComment());
        pcAudit.setConfidence(request.getConfidence());
        pcAudit.setAudited(true);
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
        conferenceRepository.save(conference);
        return true;
    }

    public void downloadThesis(Long id, HttpServletResponse response) throws BadCredentialsException {
        Thesis thesis = thesisRepository.findById(id).get();
        try (InputStream inputStream = new FileInputStream(new File(thesis.getPath()));
             OutputStream outputStream = response.getOutputStream();) {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + thesis.getFileName());
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad downloading!");
        }
    }
}
