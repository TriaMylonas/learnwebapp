package dev.triamylo.learnwebapp.configuration;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import dev.triamylo.learnwebapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


class FirstUserConfigTest extends AbstractApplicationTests {

    @Autowired
    private FirstUserConfig firstUserConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Test
    void testInitUser() {
        // Clean up the database
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Execute the initUser method
        firstUserConfig.initUser();

        // Verify that the user and role were created and associated
        Optional<User> adminUser = userRepository.findByUsername("adminUser");
        Optional<Role> adminRole = roleRepository.findByRoleName("ADMIN");

        assertEquals(true, adminUser.isPresent());
        assertEquals(true, adminRole.isPresent());
        assertEquals(1, adminUser.get().getRoles().size());
        assertEquals("ADMIN", adminUser.get().getRoles().iterator().next().getRoleName());
    }


}