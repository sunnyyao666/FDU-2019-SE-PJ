package fudan.se.lab2;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * @author YHT
 */
@SpringBootApplication
public class Lab2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

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
                    Authority adminAuthority = new Authority("Admin", admin, null, null);
                    userRepository.save(admin);
                    authorityRepository.save(adminAuthority);
                }
            }
        };
    }
}