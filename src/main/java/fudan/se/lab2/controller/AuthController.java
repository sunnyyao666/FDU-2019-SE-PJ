package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author YHT
 */
@RestController
public class AuthController {
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(AuthService authService, JwtTokenUtil jwtTokenUtil) {
        this.authService = authService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getUsername(), request.getPassword());
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.header("token", jwtTokenUtil.generateToken(user));
        return builder.body(user);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(authService.updateUser());
    }

    @PostMapping("/searchUsers")
    public ResponseEntity<?> searchUsers(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(authService.searchUsers(request.getText(), request.getConferenceFullName()));
    }

    @PostMapping("/invitePCMember")
    public ResponseEntity<?> invitePCMember(@RequestBody InvitePCMemberRequest request) {
        String[] invitee = request.getInvitee();
        for (String username : invitee) authService.invitePCMember(username, request.getConferenceFullName());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/listInviteHistory")
    public ResponseEntity<?> listInviteHistory(@RequestBody AuditApplicationRequest request) {
        return ResponseEntity.ok(authService.listInviteHistory(request.getConferenceFullName()));
    }

    @PostMapping("/auditPCInvitationApplication")
    public ResponseEntity<?> auditPCInvitationApplication(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(authService.auditPCInvitationApplication(request.getConferenceFullName(), request.getText()));
    }
}