package dev.triamylo.learnwebapp.repository;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

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
        transaction.execute(status -> {
            roleRepository.deleteAll();
            userRepository.deleteAll();
            return null;
        });
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
        role.setRoleName("role100");
        roleRepository.save(role); // save him to the DB
        assertTrue(roleRepository.findByRoleName("role100").isPresent());

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
        assertTrue(roleRepository.findByRoleName("role2").isPresent());

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

        //1 transaction with the DB,|->create user, role and save .
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

        //2 transaction with the DB,|-> assign a role in the user and save
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

        //3 transaction with the DB,|-> only read and check
        transaction.execute(status -> {
            //Read the user from the database and check if he has role
            var optionalUser = userRepository.findByUsername("tria");
            assertTrue(optionalUser.isPresent());
            var user = optionalUser.get();
            assertFalse(user.getRoles().isEmpty());
            assertEquals(1, user.getRoles().size());


            // Reload updates the role and check if the has a user
            var optionalRole = roleRepository.findByRoleName("role1");
            assertTrue(optionalRole.isPresent());
            var role1 = optionalRole.get();
            assertFalse(role1.getUsers().isEmpty());
            assertEquals(1, role1.getUsers().size());
            assertEquals(user.getUuid(), role1.getUsers().get(0).getUuid());

            return null;
        });

        //4  transaction with the DB,|->get the user and delete him.
        transaction.execute(status -> {
            //get the user
            var optionalUser = userRepository.findByUsername("tria");
            assertTrue(optionalUser.isPresent());
            var user = optionalUser.get();

            assertEquals(1, user.getRoles().size());

            // Delete the existing user
            userRepository.delete(user);
            optionalUser = userRepository.findByUsername(user.getUsername());
            assertFalse(optionalUser.isPresent());

            // Important - existing role1.getUsers() is not automatically delete!

            return null;
        });

        //5 transaction with the DB|-> read from the DB and check
        transaction.execute(status -> {
            // Reload updates the role
            var optionalRole = roleRepository.findByRoleName("role1");
            assertTrue(optionalRole.isPresent());
            var role1 = optionalRole.get();
            assertTrue(role1.getUsers().isEmpty());

            return null;
        });
    }


    @Test
    void twoUserWithTwoRolesWeDeleteOneUserAndTheTwoRoleStay() {

        //1 transaction with the DB, | we create and save the 2 users and the 2 roles
        transaction.execute(execute -> {

            User user3 = getNewUserWithNullUuid("user3");
            userRepository.save(user3);

            User user4 = getNewUserWithNullUuid("user4");
            userRepository.save(user4);

            Role role3 = new Role("role3");
            roleRepository.save(role3);

            Role role4 = new Role("role4");
            roleRepository.save(role4);

            return null;
        });

        /*2 transaction with the DB|-> I check the DB, add the roles to the users and save */
        transaction.execute(execute -> {

            var optionalUser3 = userRepository.findByUsername("user3");
            assertTrue(optionalUser3.isPresent());
            var user3 = optionalUser3.get();

            var optionalUser4 = userRepository.findByUsername("user4");
            assertTrue(optionalUser4.isPresent());
            var user4 = optionalUser4.get();

            var optionalRole3 = roleRepository.findByRoleName("role3");
            assertTrue(optionalRole3.isPresent());
            var role3 = optionalRole3.get();

            var optionalRole4 = roleRepository.findByRoleName("role4");
            assertTrue(optionalRole4.isPresent());
            var role4 = optionalRole4.get();


            user3.getRoles().add(role3);
            user3.getRoles().add(role4);

            user4.getRoles().add(role3);
            user4.getRoles().add(role4);

            userRepository.save(user3);
            userRepository.save(user4);

            return null;
        });

        /*3 transaction with DB|-> check if the users have the roles, and delete one user*/
        transaction.execute(execute -> {

            var optionalUser3 = userRepository.findByUsername("user3");
            assertTrue(optionalUser3.isPresent());
            var user3 = optionalUser3.get();

            var optionalUser4 = userRepository.findByUsername("user4");
            assertTrue(optionalUser4.isPresent());
            var user4 = optionalUser4.get();

            var optionalRole3 = roleRepository.findByRoleName("role3");
            assertTrue(optionalRole3.isPresent());
            var role3 = optionalRole3.get();

            var optionalRole4 = roleRepository.findByRoleName("role4");
            assertTrue(optionalRole4.isPresent());
            var role4 = optionalRole4.get();

            assertEquals(2, user3.getRoles().size());
            assertTrue(user3.getRoles().contains(role3));
            assertTrue(user3.getRoles().contains(role4));

            assertEquals(2, user4.getRoles().size());
            assertTrue(user4.getRoles().contains(role3));
            assertTrue(user4.getRoles().contains(role4));

            userRepository.delete(user3);
            assertFalse(userRepository.findByUsername("user3").isPresent());

            return null;
        });

        /*4 transaction with DB|-> check if the two roles are there but only the one user*/
        transaction.execute(execute -> {

            var optionalUser3 = userRepository.findByUsername("user3");
            assertFalse(optionalUser3.isPresent());


            var optionalUser4 = userRepository.findByUsername("user4");
            assertTrue(optionalUser4.isPresent());
            var user4 = optionalUser4.get();

            var optionalRole3 = roleRepository.findByRoleName("role3");
            assertTrue(optionalRole3.isPresent());
            var role3 = optionalRole3.get();

            var optionalRole4 = roleRepository.findByRoleName("role4");
            assertTrue(optionalRole4.isPresent());
            var role4 = optionalRole4.get();

            assertEquals(2, user4.getRoles().size());
            assertTrue(user4.getRoles().contains(role3));
            assertTrue(user4.getRoles().contains(role4));

            assertEquals(1, role3.getUsers().size());
            assertEquals(1, role4.getUsers().size());

            return null;
        });
    }

    @Test
    void twoUserWithTwoRolesWeDeleteOneRoleAndTheTwoUsersStay() {

        /*1 transaction with the DB -> create the users, roles and save them to DB */
        transaction.execute(execute -> {

            User user5 = getNewUserWithNullUuid("user5");
            userRepository.save(user5);

            User user6 = getNewUserWithNullUuid("user6");
            userRepository.save(user6);

            Role role5 = new Role("role5");
            roleRepository.save(role5);

            Role role6 = new Role("role6");
            roleRepository.save(role6);

            return null;
        });
        /*2 transaction with the DB -> check if the users and the roles exist and assign the roles to the users */
        transaction.execute(execute -> {

            var optionUser5 = userRepository.findByUsername("user5");
            assertTrue(optionUser5.isPresent());
            var user5 = optionUser5.get();

            var optionUser6 = userRepository.findByUsername("user6");
            assertTrue(optionUser6.isPresent());
            var user6 = optionUser6.get();

            var optionRole5 = roleRepository.findByRoleName("role5");
            assertTrue(optionRole5.isPresent());
            var role5 = optionRole5.get();

            var optionRole6 = roleRepository.findByRoleName("role6");
            assertTrue(optionRole6.isPresent());
            var role6 = optionRole6.get();


            user5.getRoles().add(role5);
            user5.getRoles().add(role6);
            userRepository.save(user5);

            user6.getRoles().add(role5);
            user6.getRoles().add(role6);
            userRepository.save(user6);

            return null;
        });

        /*3 transaction with the DB -> check if the users have the roles and then delete one Role(role5)*/
        transaction.execute(execute -> {

            var optionUser5 = userRepository.findByUsername("user5");
            assertTrue(optionUser5.isPresent());
            var user5 = optionUser5.get();

            var optionUser6 = userRepository.findByUsername("user6");
            assertTrue(optionUser6.isPresent());
            var user6 = optionUser6.get();

            var optionRole5 = roleRepository.findByRoleName("role5");
            assertTrue(optionRole5.isPresent());
            var role5 = optionRole5.get();

            var optionRole6 = roleRepository.findByRoleName("role6");
            assertTrue(optionRole6.isPresent());
            var role6 = optionRole6.get();

            assertEquals(2, user5.getRoles().size());
            assertTrue(user5.getRoles().contains(role5));
            assertTrue(user5.getRoles().contains(role6));

            assertEquals(2, user6.getRoles().size());
            assertTrue(user6.getRoles().contains(role5));
            assertTrue(user6.getRoles().contains(role6));

            // I must de coupling the user from the roles and the roles from the user before I delete something
            role5.getUsers().forEach(user -> user.getRoles().remove(role5));
            role5.getUsers().clear();
            roleRepository.delete(role5);
            assertFalse(roleRepository.findByRoleName("role5").isPresent());

            return null;
        });


        /*4 transaction with the DB -> check if the user have one role and the remain role has two users */
        transaction.execute(execute -> {

            var optionUser5 = userRepository.findByUsername("user5");
            assertTrue(optionUser5.isPresent());
            var user5 = optionUser5.get();

            var optionUser6 = userRepository.findByUsername("user6");
            assertTrue(optionUser6.isPresent());
            var user6 = optionUser6.get();

            var optionRole5 = roleRepository.findByRoleName("role5");
            assertFalse(optionRole5.isPresent());

            var optionRole6 = roleRepository.findByRoleName("role6");
            assertTrue(optionRole6.isPresent());
            var role6 = optionRole6.get();

            assertEquals(1, user5.getRoles().size());
            assertTrue(user5.getRoles().contains(role6));

            assertEquals(1, user6.getRoles().size());
            assertTrue(user6.getRoles().contains(role6));


            return null;
        });


    }


}




































