package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractMockUpTests;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.ModelMap;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class IndexControllerMockMvcTest extends AbstractMockUpTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private String uuid;


    @BeforeEach
    void setUp() {
        // Clean database before test
        repository.deleteAll();

        // Prepare users in database
        for (int i = 1; i < 11; i++) {
            User u = new User();
//            u.setUuid("uuid-" + i); // Is set by org.hibernate.annotations.UuidGenerator
            u.setUsername(String.valueOf(i));
            u.setFirstName("firstName-" + i);
            u.setLastName("lastName-" + i);
            u.setDob(LocalDate.of(1992, 1, i));
            u.setHeight(180 + i);
            repository.save(u);
            uuid = u.getUuid();
        }
        assertEquals(10, repository.count());
    }


    @Test
    void startSite() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void formulaForNewUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/formula"))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void usersAdminRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("users"));

        //I will take the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andReturn()
                .getModelAndView()
                .getModel();

        assertFalse(modelMap.isEmpty());

        //I save the model to a list, so I can check the values of the list too.
        List<User> users = (List<User>) modelMap.get("users");
        assertFalse(users.isEmpty());
        assertNotNull(users);
        assertEquals(10, users.size());
        assertEquals("firstName-2", users.get(1).getFirstName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usersUserRole() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "NONE")
    void usersNoneRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdminPositiv() throws Exception {

        //create the request
        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"))
                .andExpect(model().attributeExists("user"));

        //I will take again the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", uuid))
                .andReturn()
                .getModelAndView()
                .getModel();

        //now save it in to a User Object, so we can validate.
        User user = (User) modelMap.get("user");
        assertNotNull(user);


        //negative test, if the uuid is null -> return "redirect:/users"
        //create the service I need for the User user = userService.get(uuid) from the controller
//        given(userService.get(targetUuid)).willReturn(null);

        String nullUuid = "xxx";
        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", nullUuid))
                .andExpect(status().is3xxRedirection()) //302 redirect
                .andExpect(view().name("redirect:/users"));
    }

    @Test
    void updateNegative() throws Exception {
        //create request without roles
        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", uuid))
                .andExpect(status().is3xxRedirection()); //302 redirect
    }

    @Test
    @WithMockUser(roles = "USER", username = "1")
    void seeOnlyYourDataPositiv() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/me"))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"));
    }

    @Test
    @WithMockUser(roles = "NONE" ,username = "1")
    void seeOnlyYourDataWithNoneRoleSeeOtherUserData() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/me"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/ErrorNotAuthorized"));
    }

    @Test
    //We have only user in our test repository with username from 1 to 10
    @WithMockUser(roles = "User" ,username = "random")
    void seeOnlyYourDataWithUserRoleSeeOtherRandomUserData() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/me"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWithAdminRole() throws Exception {

        // create a user with a specific UUID that I expect to be returned by my service
        String targetUuid = "uuid-1";

        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{uuid}", targetUuid))
                //I give not status ok back, I am redirect only a view.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteWithUserRole() throws Exception{
        Optional<User> optionalUser = repository.findByUsername("1");
        assertTrue(optionalUser.isPresent());

        String userUuid = optionalUser.get().getUuid();
        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{uudi}",userUuid))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerSiteAdminRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("username", "11")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void registerSiteUserRoleAddNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("username", "12")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));
    }

    @Test
    @WithMockUser(roles = "NONE")
    void registerSiteNoneRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("username", "30")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));
    }

    @Test
    @WithMockUser()
    void registerSiteWrongDayOfBirthNegative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("username", "1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "0111-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void registerSiteWithUserRoleAndUpdateHisOwnData() throws Exception {
        // I take the user because I don't know the auto generated uuid of the object
        Optional<User> userOptional = repository.findByUsername("1");

        assertTrue(userOptional.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("uuid", userOptional.get().getUuid())
                        .param("username", "1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk());

        //I check if the changes has done, on the test database
        Optional<User> testUser = repository.findByUsername("1");
        assertTrue(testUser.isPresent());
        assertEquals("John", testUser.get().getFirstName());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void registerSiteWithUserRoleAndUpdateOtherUserData() throws Exception {
        // I take another username.
        Optional<User> testUser = repository.findByUsername("2");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("uuid", testUser.get().getUuid())
                        .param("username", "2")
                        .param("firstName", "TestName")
                        .param("lastName", "testLastname")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error/ErrorNotAuthorized"));

        //check if the user change
        assertNotEquals("TestName", testUser.get().getFirstName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerSiteWithAdminRoleUpdateOtherUserData() throws Exception {
        // I take another username.
        Optional<User> testUser = repository.findByUsername("2");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("uuid", testUser.get().getUuid())
                        .param("username", "2")
                        .param("firstName", "TestName")
                        .param("lastName", "testLastname")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));

        //check if the user change
        User updatedTestUser = repository.findByUsername("2").get();

        assertEquals("TestName", updatedTestUser.getFirstName());
        assertEquals("testLastname", updatedTestUser.getLastName());
    }

    @Test
    @WithAnonymousUser()
    void registerSiteWithAnonymousUserUpdateOtherUserData() throws Exception{

        Optional<User> testUser = repository.findByUsername("1");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .param("uuid", testUser.get().getUuid())
                        .param("username", "1")
                        .param("firstName", "TestName")
                        .param("lastName", "testLastname")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error/ErrorNotAuthorized"));

        User updatedTestUser = repository.findByUsername("1").get();
        assertEquals(testUser.get().getFirstName(),updatedTestUser.getFirstName());
        assertEquals(testUser.get().getLastName(), updatedTestUser.getLastName());
    }

}