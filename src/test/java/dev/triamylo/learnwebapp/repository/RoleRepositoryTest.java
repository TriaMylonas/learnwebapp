package dev.triamylo.learnwebapp.repository;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



class RoleRepositoryTest extends AbstractApplicationTests {


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(){

        roleRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    @Transactional  //richtig!!!
    void addNewRole(){

        Role testRole= new Role();
        assertNull(testRole.getUuid());

        testRole.setRoleName("testname");
        //here generiert die uuid, da es generiert von die Datenbank!!
        roleRepository.save(testRole);

        Optional<Role> optionalRole = roleRepository.findByRoleName("testname");
        assertTrue(optionalRole.isPresent());

        assertEquals("testname", optionalRole.get().getRoleName());
        assertNotNull(optionalRole.get().getUuid());


        User testUser = getNewUser();
        testUser.setFirstName("testFirstName");
        testUser.setUuid(null);

        List<Role> roles = new ArrayList<>();
        roles.add(testRole);

        testUser.setRoles(roles);

        //user speicher
        userRepository.save(testUser);

        Optional<User> optionalUser = userRepository.findByFirstName("testFirstName");
        assertTrue(optionalUser.isPresent());

        assertNotNull(optionalUser.get().getRoles());

        assertEquals(1, optionalUser.get().getRoles().size());
        // ist meine Role drinnen.
        assertTrue(optionalUser.get().getRoles().contains(testRole));
    }



}