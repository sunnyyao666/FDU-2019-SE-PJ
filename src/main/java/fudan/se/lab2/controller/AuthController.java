package fudan.se.lab2.controller;

import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.JwtUserDetailsService;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBW
 */
@RestController
public class AuthController {

    private AuthService authService;
    private JwtTokenUtil jwtTokenUtil;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService,JwtTokenUtil jwtTokenUtil) {
        this.authService = authService;
        this.jwtTokenUtil=jwtTokenUtil;
    }


    @GetMapping("/register")
    public ResponseEntity<?> register(String username, String password) {
        logger.debug("RegistrationForm: " + username+password);
        return ResponseEntity.ok(authService.register(new RegisterRequest(username,password,"1",null)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
       // logger.debug("LoginForm: " + request.toString());
        User user=authService.login(request.getUsername(),request.getPassword());
        ResponseEntity.BodyBuilder builder=ResponseEntity.ok();
        builder.header("token",jwtTokenUtil.generateToken(user));
        return builder.body(user);
    }

    /**
     * This is a function to test your connectivity. (健康测试时，可能会用到它）.
     */
    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2. ";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}



