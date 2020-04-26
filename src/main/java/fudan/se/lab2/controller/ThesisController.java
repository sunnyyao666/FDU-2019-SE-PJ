package fudan.se.lab2.controller;

import fudan.se.lab2.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> submitThesis(Long id, String conferenceFullName, String title, String summary, String authors, String topics, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(thesisService.submitThesis(id, conferenceFullName, title, summary, authors, topics, file));
    }

}
