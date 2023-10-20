package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.ModelMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = IndexController.class)
class IndexControllerMockMvcTest {


    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserService userService;

    List<User> userList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        for (int i = 1; i < 11; i++) {
            User u = new User();
            u.setUuid("uuid-" + i);
            u.setFirstName("firstName-" + i);
            u.setLastName("lastName-" + i);
            u.setDob(LocalDate.of(1992, 1, i));
            u.setHeight(180 + i);
            userList.add(u);
        }
    }


    @Test
    void startSite() throws Exception {

        // I use mockMvc to send a get request to this URL "/formula"
        this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
                // Expects HTTP 200 OK
                .andExpect(status().isOk())
                // Expects the view name to be "formula"
                .andExpect(view().name("index"));
    }


    @Test
    void formulaForNewUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/formula"))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"))
                // Expects the "user" attribute in the model
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    void users() throws Exception {
        // I use my custom user list each time will called the userServices.
        // In this Test I don't need to control the Services, only the Controllers
        given(userService.list()).willReturn(userList);

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

        //i save the model to a list so I can check the values of the list too.
        List<User> users = (List<User>) modelMap.get("users");
        assertFalse(users.isEmpty());
        assertNotNull(users);
        assertEquals(10, users.size());
        assertEquals("firstName-2", users.get(1).getFirstName());
    }

    @Test
    void update() throws Exception {

        // create a user with a specific UUID that I expect to be returned by my service
        String targetUuid = "uuid-1";
        User expectedUser = new User(
                "updatedFirstName", "updatedLastName",
                LocalDate.of(1990, 5, 5), 175
        );
        expectedUser.setUuid(targetUuid);

        //create the service I need for the User user = userService.get(uuid) from the controller
        given(userService.get(targetUuid)).willReturn(expectedUser);

        //create the request
        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", targetUuid))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"))
                .andExpect(model().attributeExists("user"));

        //I will take again the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", targetUuid))
                .andReturn()
                .getModelAndView()
                .getModel();

        //now save it in to a User Object, so we can validate.
        User user = (User) modelMap.get("user");
        assertNotNull(user);
        assertEquals("uuid-1", user.getUuid());
        assertEquals("updatedFirstName", user.getFirstName());
        assertEquals("updatedLastName", user.getLastName());
        assertEquals(expectedUser.getDob(), user.getDob());
        assertEquals(expectedUser.getHeight(), user.getHeight());


        //negative test, if the uuid is null -> return "redirect:/users"
        //create the service I need for the User user = userService.get(uuid) from the controller
        given(userService.get(targetUuid)).willReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{uuid}", targetUuid))
                .andExpect(status().is3xxRedirection()) //302 redirect
                .andExpect(view().name("redirect:/users"));
    }

    @Test
    void delete() throws Exception {

        // create a user with a specific UUID that I expect to be returned by my service
        String targetUuid = "uuid-1";

        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{uuid}", targetUuid))
                //I give not status ok back, I am redirect only a view.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    void registerSite() throws Exception {

        // Create a user like the browser as a string and send it to the /formula with post
        String formData = "uuid=&firstName=testName&lastName=testLastname&dob=1995-05-05&height=55";

        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    void registerSiteNegative() throws Exception {
        // Create a user like the browser as a string and send it to the /formula with post, but this time will be
        // no valid, so I can make the negative test also. I give without name
        String falseFormData = "uuid=&firstName=&lastName=testLastname&dob=1995-05-05&height=55";
        //now I must be redirected to formula because my values was not right
        mockMvc.perform(MockMvcRequestBuilders.post("/formula")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(falseFormData))
                .andExpect(status().isOk())
                .andExpect(view().name("formula"));
    }
}