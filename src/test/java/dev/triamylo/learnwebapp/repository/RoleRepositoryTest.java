package dev.triamylo.learnwebapp.repository;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class RoleRepositoryTest extends AbstractApplicationTests {

    @Autowired
    private TransactionTemplate transaction;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
        //richtig!!!
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
    void addOneNewRoleInOneNewUser() {

        //save role in the DB and check it
        Role testRole = new Role();
        assertNull(testRole.getUuid());
        testRole.setRoleName("roleName");
        roleRepository.save(testRole);
        Optional<Role> optionalRole = roleRepository.findByRoleName("roleName");
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
    void addManyRolesInOneNewUser() {

        //create the roles and saved them to DB
        Role role1 = new Role();
        role1.setRoleName("user");
        roleRepository.save(role1);
        assertTrue(roleRepository.findByRoleName("user").isPresent());

        Role role2 = new Role();
        role2.setRoleName("admin");
        roleRepository.save(role2);
        assertTrue(roleRepository.findByRoleName("admin").isPresent());

        Role role3 = new Role();
        role3.setRoleName("none");
        roleRepository.save(role3);
        assertTrue(roleRepository.findByRoleName("none").isPresent());

        //add the roles in a List
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        roles.add(role3);

        //create a user add him the roles List and save him to the DB
        User testUser = getNewUser();
        testUser.setUuid(null);
        testUser.setUsername("tria");
        testUser.setRoles(roles);
        userRepository.save(testUser);
        assertTrue(userRepository.findByUsername("tria").isPresent());

        //after check if he has the roles
        var check = userRepository.findByUsername("tria").get();
        assertEquals(3, check.getRoles().size());
        assertTrue(check.getRoles().contains(role1));
        assertTrue(check.getRoles().contains(role2));
        assertTrue(check.getRoles().contains(role3));
    }

    @Test
    @Transactional
    void oneRoleInManyUser() {
        //create the role, add him in DB and then check it
        Role role = new Role();
        role.setRoleName("role1");
        roleRepository.save(role); // save him to the DB
        assertTrue(roleRepository.findByRoleName("role1").isPresent());

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        //create 3 users add the role to each of them and then save them to DB
        //check if all 3 users have the role
        User user1 = getNewUser();
        user1.setUuid(null); // it will be added from the DB
        user1.setUsername("user1");
        user1.setRoles(roles);
        userRepository.save(user1);
        assertTrue(userRepository.findByUsername("user1").isPresent());
        assertTrue(userRepository.findByUsername("user1").get().getRoles().contains(role));

        User user2 = getNewUser();
        user2.setUuid(null); // it will be added from the DB
        user2.setUsername("user2");
        user2.setRoles(roles);
        userRepository.save(user2);
        assertTrue(userRepository.findByUsername("user2").isPresent());
        assertTrue(userRepository.findByUsername("user2").get().getRoles().contains(role));

        User user3 = getNewUser();
        user3.setUuid(null); // it will be added from the DB
        user3.setUsername("user3");
        user3.setRoles(roles);
        userRepository.save(user3);
        assertTrue(userRepository.findByUsername("user3").isPresent());
        assertTrue(userRepository.findByUsername("user3").get().getRoles().contains(role));


    }

    @Test
    @Transactional
    void oneUserWithOneRoleGetsOneMoreRole() {
        // first role
        Role role1 = new Role();
        role1.setRoleName("role1");
        roleRepository.save(role1);
        assertTrue(roleRepository.findByRoleName("role1").isPresent());

        List<Role> roles = new ArrayList<>();
        roles.add(role1);

        User testUser = getNewUser();
        testUser.setUuid(null);
        testUser.setUsername("tria");
        testUser.setRoles(roles);
        userRepository.save(testUser);
        assertTrue(userRepository.findByUsername("tria").isPresent());
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role1));

        Role role2 = new Role();
        role2.setRoleName("role2");
        roleRepository.save(role2);
        assertTrue(roleRepository.findByRoleName("role1").isPresent());

        roles.add(role2);
        testUser.setRoles(roles);
        userRepository.save(testUser);
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role1));
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role2));
    }

    @Test
    @Transactional
    void oneUserWithOneRoleChangeThatRole() {

        Role role1 = new Role();
        role1.setRoleName("role1");
        roleRepository.save(role1);
        assertTrue(roleRepository.findByRoleName("role1").isPresent());

        List<Role> roles = new ArrayList<>();
        roles.add(role1);

        User testuser = getNewUser();
        testuser.setUuid(null);
        testuser.setUsername("tria");
        testuser.setRoles(roles);
        userRepository.save(testuser);
        assertTrue(userRepository.findByUsername("tria").isPresent());
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role1));

        Role role2 = new Role();
        role2.setRoleName("role2");
        roleRepository.save(role2);
        assertTrue(roleRepository.findByRoleName("role2").isPresent());

        roles.set(0, role2);
        testuser.setRoles(roles);
        userRepository.save(testuser);
        assertFalse(userRepository.findByUsername("tria").get().getRoles().contains(role1));
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role2));
    }

    @Test
    @Transactional
    void oneUserWithOneRoleDeleteThatRole() {

        Role role1 = new Role();
        role1.setRoleName("role1");
        roleRepository.save(role1);
        assertTrue(roleRepository.findByRoleName("role1").isPresent());

        List<Role> roles = new ArrayList<>();
        roles.add(role1);

        User user = getNewUser();
        user.setUuid(null);
        user.setUsername("tria");
        user.setRoles(roles);
        userRepository.save(user);
        assertTrue(userRepository.findByUsername("tria").isPresent());
        assertTrue(userRepository.findByUsername("tria").get().getRoles().contains(role1));

        roles.remove(role1);
        var testUser = userRepository.findByUsername("tria").get();
        testUser.setRoles(roles);
        userRepository.save(testUser);
        assertTrue(testUser.getRoles().isEmpty());
        assertFalse(testUser.getRoles().contains(role1));
    }

    @Test
    void userDeletedButNotTheRolesWithHim() {

        //each transaction write or read something in the DB
        //fist transaction with the DB, create user and role.
        transaction.execute(status -> {

            // User created without roles
            User user = getNewUser();
            user.setUuid(null);
            user.setUsername("tria");
            userRepository.save(user);

            // Create new role without users
            Role role1 = new Role();
            role1.setRoleName("role1");
            roleRepository.save(role1);

            //write those two in the DB
            return null;
        });

        //second transaction with the DB, assign a role in the user
        transaction.execute(status -> {
            //read user from the database.
            var optionalUser = userRepository.findByUsername("tria");
            assertTrue(optionalUser.isPresent());
            var user = optionalUser.get();
            assertTrue(user.getRoles().isEmpty());

            // Read new role from database
            var optionalRole = roleRepository.findByRoleName("role1");
            assertTrue(optionalRole.isPresent());
            var role1 = optionalRole.get();
            assertTrue(role1.getUsers().isEmpty());

            // Assign user a role
            user.getRoles().add(role1);
            userRepository.save(user);

            // Important - existing role is not automatically updates
            assertTrue(role1.getUsers().isEmpty());

            return null;
        });
        //third transaction with the database, delete the user
        transaction.execute(status -> {
            //Read the user from the database and check if he has role
            var optionalUser = userRepository.findByUsername("tria");
            assertTrue(optionalUser.isPresent());
            var user = optionalUser.get();

            assertEquals(1,user.getRoles().size());

            // Reload updates the role and check if the has a user
            var optionalRole = roleRepository.findByRoleName("role1");
            assertTrue(optionalRole.isPresent());
            var role1 = optionalRole.get();

            assertFalse(role1.getUsers().isEmpty());
            assertEquals(1, role1.getUsers().size());
            assertEquals(user.getUuid(), role1.getUsers().get(0).getUuid());

            return null;
        });

        //fourth transaction we are deleting the user
        transaction.execute(status -> {
            //get the user
            var optionalUser = userRepository.findByUsername("tria");
            assertTrue(optionalUser.isPresent());
            var user = optionalUser.get();

            assertEquals(1,user.getRoles().size());

            // Delete the existing user
            userRepository.delete(user);
            optionalUser = userRepository.findByUsername(user.getUsername());
            assertFalse(optionalUser.isPresent());

            // Important - existing role1.getUsers() is not automatically delete!

            return null;
        });

        transaction.execute(status -> {
            // Reload updates the role
            var optionalRole = roleRepository.findByRoleName("role1");
            assertTrue(optionalRole.isPresent());
            var role1 = optionalRole.get();
            assertTrue(role1.getUsers().isEmpty());

            return null;
        });

//        roles.add(role1);
//
//        Role role2 = new Role("role2");
//        role2.setUsers(users);
//        roleRepository.save(role2);
//        assertTrue(roleRepository.findByRoleName("role2").isPresent());
//        assertTrue(roleRepository.findByRoleName("role2").get().getUsers().contains(user));
//
//        roles.add(role2);
//
//        //delete user
//        userRepository.delete(user);
//        assertFalse(userRepository.findByUsername("tria").isPresent());


//        assertEquals(2, roleRepository.count());
//
//        assertTrue(roleRepository.findByRoleName("role1").isPresent());
//        assertFalse(roleRepository.findByRoleName("role1").get().getUsers().contains(user));
//
//        assertTrue(roleRepository.findByRoleName("role2").isPresent());
//        assertFalse(roleRepository.findByRoleName("role2").get().getUsers().contains(user));
    }
}