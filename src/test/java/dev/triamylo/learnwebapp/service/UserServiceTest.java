package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {


    private  UserService userService;
    @BeforeEach
    void setUp(){
        userService = new UserService();
    }



    @Test
    @DisplayName("positive- add(User) from service, if adds a User on a list")
    void testAdd() {

        // create a new user
        User testUser = new User("Tria", "Mylo", LocalDate.of(1970, 1, 1), 195);

        //add the user to the service
        userService.add(testUser);

        //take the ID of the user that I have added.
        String testUserId = testUser.getUuid();


        //I get new user from the list that the service saved him.
        User getTheTestUser = userService.get(testUserId);

        //I compare the user that I took with the values that I put on the start, so I know everything working good.
        assertNotNull(getTheTestUser);
        assertEquals(testUser,getTheTestUser);
        assertEquals("Tria",getTheTestUser.getFirstName());
        assertEquals("Mylo",getTheTestUser.getLastName());
        assertEquals(195,getTheTestUser.getHeight());
    }

    @Test
    @DisplayName("positive- list() of the service,if it returns a list of Users")
    void testList(){

        //I create 3 Users.
        User testUser1 = new User("Tria1", "Mylo1", LocalDate.of(1971, 1, 1), 195);
        User testUser2 = new User("Tria2", "Mylo2", LocalDate.of(1972, 2, 2), 200);
        User testUser3 = new User("Tria3", "Mylo3", LocalDate.of(1973, 3, 3), 205);

        //add the users to the service list.
        userService.add(testUser1);
        userService.add(testUser2);
        userService.add(testUser3);

        //I recall the list from the service
        List<User> resultUsersList = userService.list();

        // I see if the list is empty and if is 3 object inside.
        assertFalse(resultUsersList.isEmpty());
        assertEquals(3,resultUsersList.size());

        //I compare if the values of the object in the list is same with that I put in the start.
        assertEquals("Tria1", resultUsersList.get(0).getFirstName());
        assertEquals(LocalDate.of(1971,1,1), resultUsersList.get(0).getDob());

        assertEquals("Tria2", resultUsersList.get(1).getFirstName());
        assertEquals(200,resultUsersList.get(1).getHeight());

        assertEquals("Mylo3", resultUsersList.get(2).getLastName());
        assertEquals("1973-03-03", resultUsersList.get(2).getDob().toString());

    }

    @Test
    @DisplayName("positive- delete(User id) of the service, if it delete a user.")
    void delete() {

        //create a user
        User testUser1 = new User("Tria1", "Mylo1", LocalDate.of(1971, 1, 1), 195);

        //add the user to the service list and take this list.
        userService.add(testUser1);
        List<User> userList = userService.list();

        //check if the user is added to the list
        assertFalse(userList.isEmpty());
        assertEquals(1, userList.size());
        assertEquals("Tria1", userList.get(0).getFirstName());

        //delete the User from the list. (We need to pass the ID as String for parameter)
        userService.delete(testUser1.getUuid());

        //check if the user is deleted and the method works how it supposes to.
        assertTrue(userList.isEmpty());
        assertFalse(userList.contains(testUser1));
    }

    @Test
    @DisplayName("positive- (User id) of the service, if returns the user")
    void get() {

        //create a user first of all :)
        User testUser1 = new User("Tria1", "Mylo1", LocalDate.of(1971, 1, 1), 195);

        //save the user to the list of the service, so we can play with him later :)
        userService.add(testUser1);

        //we take the list to proof that the user saved and his last name it's "Mylo1"
        List<User> userList = userService.list();
        assertFalse(userList.isEmpty());
        assertEquals("Mylo1", userList.get(0).getLastName());

        //we get the user from the list by using the get() method we wanna to proof!!!
        User newTestUser = userService.get(userList.get(0).getUuid());

        //we check if we have take the user that we saved in the first place, and the method work correct!$§
        assertEquals("Tria1", newTestUser.getFirstName());
        assertEquals("Mylo1", newTestUser.getLastName());
        assertEquals("1971-01-01", newTestUser.getDob().toString());
        assertEquals(195, newTestUser.getHeight());

        //Yes! we did it! we passt all the Test!!!
    }

    @Test
    @DisplayName("positive- update(User) of the service, if update one already saved user")
    void update() {

        //create a user
        User testUser1 = new User("Tria1", "Mylo1", LocalDate.of(1971, 1, 1), 195);

        //add them to the service list. get this list for testing.
        userService.add(testUser1);
        //the user take the id in the add method random.
        String testUser1Uuid = testUser1.getUuid();
        List<User> userList = userService.list();

        //check quick the list, that all is alright
        assertFalse(userList.isEmpty());
        assertEquals("Tria1", userList.get(0).getFirstName());

        //create a new deference user.
        User testUser2 = new User("Tria2", "Mylo2", LocalDate.of(1972, 2, 2), 200);
        //we change the id of the second object, so that the update method things is the same with difference values.
        testUser2.setUuid(testUser1Uuid);

        //update the first user of our list with the update method we wanna tested!
        userService.update(testUser2);
        List<User> userList2 = userService.list();

        //we check if all work as they should !!
        assertFalse(userList2.isEmpty());
        assertEquals("Tria2", userList2.get(0).getFirstName());
        assertEquals("Mylo2", userList2.get(0).getLastName());
        assertEquals(200, userList2.get(0).getHeight());

    }
}