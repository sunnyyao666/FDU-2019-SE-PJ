package fudan.se.lab2.controller;

import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.controller.request.ConferenceApplyRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author YHT
 */
@RestController
public class AuthController {

    private AuthService authService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(AuthService authService,JwtTokenUtil jwtTokenUtil) {
        this.authService = authService;
        this.jwtTokenUtil=jwtTokenUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user=authService.login(request.getUsername(),request.getPassword());
        ResponseEntity.BodyBuilder builder=ResponseEntity.ok();
        builder.header("token",jwtTokenUtil.generateToken(user));
        return builder.body(user);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> conferenceApply(@RequestBody ConferenceApplyRequest request) {
        
        return ResponseEntity.ok(authService.conferenceApply(request));
    }

}



