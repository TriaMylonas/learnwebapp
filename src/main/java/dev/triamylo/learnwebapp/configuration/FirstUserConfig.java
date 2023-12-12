package dev.triamylo.learnwebapp.configuration;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import dev.triamylo.learnwebapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class FirstUserConfig {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionTemplate transaction;
    private final PasswordEncoder passwordEncoder;

    public FirstUserConfig(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, TransactionTemplate transaction) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transaction = transaction;
    }


    //The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization.
    @PostConstruct
    public void initUser() {

        transaction.execute(status -> {

            // here is the role
            Optional<Role> optionalAdminRole = roleRepository.findByRoleName("ADMIN");
            if (optionalAdminRole.isEmpty()) {
                Role adminRole = new Role("ADMIN");
                roleRepository.save(adminRole);
                optionalAdminRole = roleRepository.findByRoleName("ADMIN");
            }

            // here is the user
            Optional<User> optionalAdminUser = userRepository.findByUsername("adminUser");

            if (optionalAdminUser.isEmpty()) {
                User adminUser = new User("adminUser", passwordEncoder.encode("admin"), " admin", "adminLastname", LocalDate.now(), 195);
                userRepository.save(adminUser);
                optionalAdminUser = userRepository.findByUsername("adminUser");
            }

            // now that I have both I assign the role to the user and save him to the database
            User adminUser = optionalAdminUser.get();
            Role adminRole = optionalAdminRole.get();
            if (!adminUser.getRoles().contains(adminRole)) {
                adminUser.getRoles().add(adminRole);
                userRepository.save(adminUser);
            }
            return null;

        });


    }


}
