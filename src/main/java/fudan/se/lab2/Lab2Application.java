package fudan.se.lab2;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

/**
 * @author YHT
 */
@SpringBootApplication
public class Lab2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

    /**
     * This is a function to create some basic entities when the application starts.
     */
    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // Create an admin if not exists.
                String[] region = {"Asia", "China"};
                if (userRepository.findByUsername("admin") == null) {
                    User admin = new User(
                            "admin",
                            passwordEncoder.encode("password"),
                            "Yao Hongtao",
                            "18302010017@fudan.edu.cn",
                            "Fudan University",
                            region
                    );
                    Authority adminAuthority = new Authority("Admin", admin, null);
                    userRepository.save(admin);
                    authorityRepository.save(adminAuthority);
                }
                userRepository.save(new User("11111", passwordEncoder.encode("123456"), "Zhang Yi", "1@163.com", "1", region));
                userRepository.save(new User("22222", passwordEncoder.encode("123456"), "Zhang Er", "2@163.com", "2", region));
                userRepository.save(new User("33333", passwordEncoder.encode("123456"), "Zhang San", "3@163.com", "3", region));
                userRepository.save(new User("44444", passwordEncoder.encode("123456"), "Zhang Si", "4@163.com", "4", region));
                userRepository.save(new User("55555", passwordEncoder.encode("123456"), "Zhang Wu", "5@163.com", "5", region));
                conferenceRepository.save(new Conference("12", "12345", "1", new Date(2020, 4, 7), new Date(2020, 4, 7), new Date(2020, 5, 7), new Date(2020, 5, 10), userRepository.findByUsername("11111")));
                Conference conference = new Conference("24", "23456", "1", new Date(2020, 4, 7), new Date(2020, 4, 7), new Date(2020, 5, 7), new Date(2020, 5, 10), userRepository.findByUsername("11111"));
                conference.setApplying(false);
                conference.setValid(true);
                conferenceRepository.save(conference);
            }
        };
    }
}