package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.AuditApplicationRequest;
import fudan.se.lab2.controller.request.AuditThesisRequest;
import fudan.se.lab2.controller.request.SearchRequest;
import fudan.se.lab2.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YHT
 */
@RestController
public class ThesisController {
    private ThesisService thesisService;

    @Autowired
    public ThesisController(ThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitThesis(@RequestParam("id") Long id, @RequestParam("conferenceFullName") String conferenceFullName, @RequestParam("title") String title, @RequestParam("summary") String summary, @RequestParam("authors") String authors, @RequestParam("topics") String topics, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(thesisService.submitThesis(id, conferenceFullName, title, summary, authors, topics, file));
    }

    @PostMapping("/startAudit")
    public ResponseEntity<?> startAudit(@RequestBody AuditApplicationRequest request) {
        if (request.isPassed())
            return ResponseEntity.ok(thesisService.startAudit1(request.getConferenceFullName()));
        else return ResponseEntity.ok(thesisService.startAudit2(request.getConferenceFullName()));
    }

    @PostMapping("/pcGetTheses")
    public ResponseEntity<?> pcGetTheses(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(thesisService.pcGetTheses(request.getConferenceFullName()));
    }

    @PostMapping("/auditThesis")
    public ResponseEntity<?> auditThesis(@RequestBody AuditThesisRequest auditThesisRequest) {
        return ResponseEntity.ok(thesisService.auditThesis(auditThesisRequest));
    }

    @PostMapping("/endAudit")
    public ResponseEntity<?> endAudit(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(thesisService.endAudit(request.getConferenceFullName()));
    }

    @GetMapping("/downloadThesis?id={id}")
    public void downloadThesis(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        thesisService.downloadThesis(id,request,response);
    }
}
