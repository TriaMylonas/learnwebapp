package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractMockUpTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import dev.triamylo.learnwebapp.repository.UserRepository;
import dev.triamylo.learnwebapp.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.ModelMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserControllerMockMvcTest extends AbstractMockUpTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    private String uuid;

    private String roleUuid;

    @BeforeEach
    void setUp() {
        // Clean database before test
        userRepository.deleteAll();

        // Prepare users in database
        for (int i = 1; i < 11; i++) {
            User u = new User();
//            u.setUuid("uuid-" + i); // Is set by org.hibernate.annotations.UuidGenerator
            u.setUsername(String.valueOf(i));
            u.setPassword("pass-" + i);
            u.setFirstName("firstName-" + i);
            u.setLastName("lastName-" + i);
            u.setDob(LocalDate.of(1992, 1, i));
            u.setHeight(180 + i);
            userRepository.save(u);
            uuid = u.getUuid();
        }
        assertEquals(10, userRepository.count());


        roleRepository.deleteAll();
        Role role1 = new Role("role1");
        role1.setRoleDescription("description1");
        roleRepository.save(role1);
        roleUuid = role1.getUuid();
    }


    @Test
    void startSite() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void formulaForNewUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userFormula"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getListAdminRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userList"))
                .andExpect(model().attributeExists("users"));

        //I will take the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/user/list"))
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
    void getListUserRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "NONE")
    void usersNoneRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdminPositiv() throws Exception {

        //create the request
        mockMvc.perform(MockMvcRequestBuilders.get("/user/update/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userFormula"))
                .andExpect(model().attributeExists("user"));

        //I will take again the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/user/update/{uuid}", uuid))
                .andReturn()
                .getModelAndView()
                .getModel();

        //now save it in to a User Object, so we can validate.
        User user = (User) modelMap.get("user");
        assertNotNull(user);


        //negative test, if the uuid is null -> return "redirect:/user/list"
        //create the service I need for the User user = userService.get(uuid) from the controller
//        given(userService.get(targetUuid)).willReturn(null);

        String nullUuid = "xxx";
        mockMvc.perform(MockMvcRequestBuilders.get("/user/update/{uuid}", nullUuid))
                .andExpect(status().is3xxRedirection()) //302 redirect
                .andExpect(view().name("redirect:/user/list"));
    }

    @Test
    void updateNegative() throws Exception {
        //create request without roles
        mockMvc.perform(MockMvcRequestBuilders.get("/user/update/{uuid}", uuid))
                .andExpect(status().is3xxRedirection()); //302 redirect
    }

    @Test
    @WithMockUser(roles = "USER", username = "1")
    void seeOnlyYourDataPositiv() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userFormula"));
    }

    @Test
    @WithMockUser(roles = "NONE", username = "1")
    void seeOnlyYourDataWithNoneRoleSeeOtherUserData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/ErrorNotAuthorized"));
    }

    @Test
    //We have only user in our test repository with username from 1 to 10
    @WithMockUser(roles = "User", username = "random")
    void seeOnlyYourDataWithUserRoleSeeOtherRandomUserData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWithAdminRole() throws Exception {

        // create a user with a specific UUID that I expect to be returned by my service
        String targetUuid = "uuid-1";

        mockMvc.perform(MockMvcRequestBuilders.get("/user/delete/{uuid}", targetUuid))
                //I give not status ok back, I am redirect only a view.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWithAdminRoleButUserDontExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/delete/{uuid}", "non"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/list"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteWithUserRole() throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername("1");
        assertTrue(optionalUser.isPresent());

        String userUuid = optionalUser.get().getUuid();
        mockMvc.perform(MockMvcRequestBuilders.get("/user/delete/{uudi}", userUuid))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectAdminRoleAddNewUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("username", "11")
                        .param("password", "password")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectAdminRoleUpdateOtherUserData() throws Exception {
        // I take another username.
        Optional<User> testUser = userRepository.findByUsername("2");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("uuid", testUser.get().getUuid())
                        .param("username", "2")
                        .param("firstName", "TestName")
                        .param("lastName", "testLastname")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/list"));

        //check if the user change
        User updatedTestUser = userRepository.findByUsername("2").get();

        assertEquals("TestName", updatedTestUser.getFirstName());
        assertEquals("testLastname", updatedTestUser.getLastName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void postObjectUserRoleAddNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("username", "12")
                        .param("password", "pass")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void postObjectWithUserRoleUpdateHisOwnData() throws Exception {
        // I take the user because I don't know the auto generated uuid of the object
        Optional<User> userOptional = userRepository.findByUsername("1");

        assertTrue(userOptional.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("uuid", userOptional.get().getUuid())
                        .param("username", "1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));

        //I check if the changes has done, on the test database
        Optional<User> testUser = userRepository.findByUsername("1");
        assertTrue(testUser.isPresent());
        assertEquals("John", testUser.get().getFirstName());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void registerSiteWithUserRoleAndUpdateOtherUserData() throws Exception {
        // I take another username.
        Optional<User> testUser = userRepository.findByUsername("2");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
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
    @WithMockUser(roles = "NONE")
    void postObjectNoneRoleAddNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("username", "30")
                        .param("password", "pass")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success/SuccessfullyAdded"));
    }

    @Test
    @WithAnonymousUser()
    void postObjectNoneRoleUpdateOtherUserData() throws Exception {

        Optional<User> testUser = userRepository.findByUsername("1");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("uuid", testUser.get().getUuid())
                        .param("username", "1")
                        .param("firstName", "TestName")
                        .param("lastName", "testLastname")
                        .param("dob", "1990-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error/ErrorNotAuthorized"));

        User updatedTestUser = userRepository.findByUsername("1").get();
        assertEquals(testUser.get().getFirstName(), updatedTestUser.getFirstName());
        assertEquals(testUser.get().getLastName(), updatedTestUser.getLastName());
    }

    @Test
    @WithMockUser()
    void postObjectWrongDayOfBirthNegative() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/post")
                        .param("username", "1")
                        .param("password", "pass")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("dob", "0111-01-01")
                        .param("height", "180")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userFormula"));
    }


    @Test
    @WithMockUser
    void postUserAddRole() throws Exception {

        Optional<User> testUser = userRepository.findByUsername("1");
        assertTrue(testUser.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + testUser.get().getUuid() + "/addRole")
                        .param("roleUuid", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/update/" + testUser.get().getUuid()));
    }

    @Test
    @WithMockUser
    void deleteRoleFromUser() throws Exception {

        Optional<User> testUser = userRepository.findByUsername("1");
        assertTrue(testUser.isPresent());

        Optional<Role> optionalRole = roleRepository.findById(roleUuid);
        assertTrue(optionalRole.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + testUser.get().getUuid() + "/deleteRole/" + optionalRole.get().getUuid())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/update/" + testUser.get().getUuid()));
    }

    @Test
    void loadUserByUserNamePositive() {

        UserDetails userDetails = userDetailsService.loadUserByUsername("1");
        assertEquals("pass-1", userDetails.getPassword());
    }

    @Test
    void loadUserByUserNameNegative() {

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("dontExist"));
    }

    @Test
    void getAuthoritiesPositive() {
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        // add some roles to my laborMouse ! ....!! !"3.1!
        //make the roles
        Role testRole1 = roleRepository.save(getNewRoleWithNullUuid("testRole1"));
        Role role2 = roleRepository.save(getNewRoleWithNullUuid("role2"));
        Role role3 = roleRepository.save(getNewRoleWithNullUuid("role3"));

        // get the user from the database and add them the roles
        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        laborUser = userRepository.findByUsername("laborMouse").get();
        laborUser.getRoles().add(testRole1);
        laborUser.getRoles().add(role2);
        laborUser.getRoles().add(role3);

        // save the Mouse to the database
        userRepository.save(laborUser);

        // let's check if all working.... :P
        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        laborUser = userRepository.findByUsername("laborMouse").get();

        var authorities = laborUser.getAuthorities();
        assertEquals(3, authorities.size());
        // I add to the roles the "ROLE_" and make them all uppercase
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_TESTROLE1")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ROLE2")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ROLE3")));
    }
    @Test
    void getAuthoritiesNegative() {
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        // let's check if all working.... :P
        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        laborUser = userRepository.findByUsername("laborMouse").get();

        var authorities = laborUser.getAuthorities();
        assertEquals(0, authorities.size());
        assertTrue(authorities.isEmpty());

        assertFalse(authorities.contains(new SimpleGrantedAuthority("NoRoles")));
    }

    @Test
    void isEnabledTest(){
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        assertTrue(userRepository.findByUsername("laborMouse").get().isEnabled());
    }

    @Test
    void isCredentialsNonExpiredTest(){
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        assertTrue(userRepository.findByUsername("laborMouse").get().isCredentialsNonExpired());
    }


    @Test
    void isAccountNonLockedTest(){
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        assertTrue(userRepository.findByUsername("laborMouse").get().isAccountNonLocked());
    }

    @Test
    void isAccountNonExpiredTest(){
        // I need a user like a labor mouse
        User laborUser = getNewUserWithNullUuid("laborMouse");
        userRepository.save(laborUser);

        assertTrue(userRepository.findByUsername("laborMouse").isPresent());
        assertTrue(userRepository.findByUsername("laborMouse").get().isAccountNonExpired());
    }


}