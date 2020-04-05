package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceApplyRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.ConferenceNameDuplicatedExecption;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AuthServiceTest
{

    @Autowired
    private AuthService AuthService;


    @Test
    @Transactional
    void conferenceApply(){
        Date testStartDate=new Date(10);
        Date testEndDate=new Date(11);
        Date testReleaseDate=new Date(2);
        Date testDDLDate=new Date(2);
        String password="111111a";
        User tesrChair = new User("testUser", AuthService.getEncoder().encode(password),"fullName", "323@d.d","off", null);
        User tesrChair1 = new User("testUser1", AuthService.getEncoder().encode(password),"fullName", "323@d.d","off", null);
        AuthService.getUserRepository().save(tesrChair);
        Conference testConference =new Conference("ABB","fullName","Place",testStartDate,testEndDate,testReleaseDate,testDDLDate,tesrChair);
        AuthService.getConferenceRepository().save(testConference);

        ConferenceApplyRequest testRequest=new ConferenceApplyRequest("fullName","abb","place",testStartDate,testEndDate,testReleaseDate,testDDLDate);
        assertThrows(ConferenceNameDuplicatedExecption.class,()->AuthService.conferenceApply(testRequest));

        ConferenceApplyRequest testRequest1=new ConferenceApplyRequest("testConferenceFullName","abb","place",testStartDate,testEndDate,testReleaseDate,testDDLDate);
        assertNotNull(AuthService.conferenceApply(testRequest1));


    }

    @Test
    @Transactional
    void register() {

        String password="111111a";
        User user = new User("testUser", AuthService.getEncoder().encode(password),"fullName", "323@d.d","off", null);
        AuthService.getUserRepository().save(user);
        RegisterRequest testRegister1=new RegisterRequest("testUser","password","fullName","testEmail@t.com","off",null);
        RegisterRequest testRegister2=new RegisterRequest("testUser1","password","fullName","testEmail@t.com","off",null);
        assertThrows(UsernameHasBeenRegisteredException.class,()->AuthService.register(testRegister1));
        assertNotNull(AuthService.register(testRegister2));


    }

    @Test
    @Transactional
    void login() {
        //AuthService service = new AuthService(null,null,null,null);
        String password="111111a";

        User user = new User("testUser", AuthService.getEncoder().encode(password),"fullName", "testEmail@t.com","off", null);
        AuthService.getUserRepository().save(user);
        assertThrows(UsernameNotFoundException.class,()->AuthService.login("testUser1","111111a"));
        assertNotNull(AuthService.login("testUser","111111a"));
        assertThrows(BadCredentialsException.class,()->AuthService.login("testUser","111112a"));

    }

}