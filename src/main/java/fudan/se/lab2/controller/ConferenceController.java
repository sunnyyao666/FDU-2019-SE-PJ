package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author YHT
 */
@RestController
public class ConferenceController {
    private ConferenceService conferenceService;

    @Autowired
    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyConference(@RequestBody ApplyConferenceRequest request) {
        return ResponseEntity.ok(conferenceService.applyConference(request));
    }

    @PostMapping("/listConferences")
    public ResponseEntity<?> listConferences(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(conferenceService.listConferences(request.getText()));
    }

    @PostMapping("/auditConferenceApplication")
    public ResponseEntity<?> auditConferenceApplication(@RequestBody AuditApplicationRequest request) {
        return ResponseEntity.ok(conferenceService.auditConferenceApplication(request.getConferenceFullName(), request.isPassed()));
    }

    @PostMapping("/searchConference")
    public ResponseEntity<?> searchConference(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(conferenceService.searchConference(request.getConferenceFullName()));
    }

    @PostMapping("/changeSubmissionState")
    public ResponseEntity<?> changeSubmissionState(@RequestBody AuditApplicationRequest request) {
        return ResponseEntity.ok(conferenceService.changeSubmissionState(request.getConferenceFullName(), request.isPassed()));
    }
}
