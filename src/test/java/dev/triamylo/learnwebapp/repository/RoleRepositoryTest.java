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
    void addNewRoleInTheDatabase() {

        Role testRole = new Role();
        assertNull(testRole.getUuid());

        testRole.setRoleName("testname");
        //here generiert die uuid, da es generiert von die Datenbank!!
        roleRepository.save(testRole);

        Optional<Role> optionalRole = roleRepository.findByRoleName("testname");
        assertTrue(optionalRole.isPresent());

        assertEquals("testname", optionalRole.get().getRoleName());
        assertNotNull(optionalRole.get().getUuid());
    }

    @Test
    @Transactional
    void addOneNewRoleInOneNewUser(){

        //save role in the DB and check it
        Role testRole = new Role();
        assertNull(testRole.getUuid());
        testRole.setRoleName("roleName");
        roleRepository.save(testRole);
        Optional<Role>  optionalRole = roleRepository.findByRoleName("roleName");
        assertTrue(optionalRole.isPresent());

        //save user in the DB and check it
        User testUser = getNewUser();
        testUser.setFirstName("userFirstName");
        testUser.setUuid(null);
        userRepository.save(testUser);
        Optional<User> optionalUser = userRepository.findByFirstName("userFirstName");
        assertTrue(optionalUser.isPresent());

        // add the role in a List and save it to the role object
        List<Role> roles = new ArrayList<>();
        roles.add(testRole);
        testUser.setRoles(roles);

        //save user with roles in DB
        userRepository.save(testUser);
        //check if the user have the roles
        Optional<User> optionalUserWithRoles = userRepository.findByFirstName("userFirstName");
        assertTrue(optionalUserWithRoles.isPresent());
        assertNotNull(optionalUserWithRoles.get().getRoles());
        assertEquals(1, optionalUser.get().getRoles().size());
        assertTrue(optionalUser.get().getRoles().contains(testRole));
    }

    @Test
    @Transactional
    void addManyRolesInOneNewUser(){

        //create the roles and saved them to DB
        Role role1 = new Role();
        role1.setRoleName("user");
        roleRepository.save(role1);

        Role role2 = new Role();

        role2.setRoleName("admin");
        Role role3 = new Role();
        role3.setRoleName("none");


        //add the roles in a List
        //create a user add him the roles List and save him to the DB

        //after check if he has the roles

    }

}