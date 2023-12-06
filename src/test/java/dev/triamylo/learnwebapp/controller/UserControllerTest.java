package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.RoleService;
import dev.triamylo.learnwebapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest extends AbstractApplicationTests {

    private UserController controller;

    @Autowired
    private IndexController indexController;

    private Model model;

    private Principal principal;

    @BeforeEach
    void setUp() {
        // κάναμε to UserService interface και με αυτό τον τρόπο είμαστε υποχρεωμένει να υλοποιήσουμε όλες τις
        // μεθόδους του.


        RoleService roleService = new RoleService() {

            private final ArrayList<Role> roles = new ArrayList<>();

            @Override
            public List<Role> list() {
                Role role1 = new Role("role1");
                roles.add(role1);
                Role role2 = new Role("role2");
                roles.add(role2);
                Role role3 = new Role("role3");
                roles.add(role3);
                return roles;
            }

            @Override
            public void add(Role role) {
                roles.add(role);
            }

            @Override
            public void delete(String uuid) {
                roles.remove(get(uuid));
            }

            @Override
            public Role get(String uuid) {
                List<Role> roles = list();
                return roles.stream().filter(role -> role.getUuid().equals(uuid)).findFirst().orElse(null);
            }

            @Override
            public void update(Role role) {
                for (Role oldRole : list()) {
                    if (oldRole.getUuid().equals(role.getUuid())) {
                        oldRole.setRoleName(role.getRoleName());
                        oldRole.setRoleDescription(role.getRoleDescription());
                    }
                }
            }

            @Override
            public Role findByName(String selectedRole) {
                List<Role> roles = list();
                return roles.stream().filter(role -> role.getRoleName().equals(selectedRole)).findFirst().orElse(null);
            }
        };
        UserService userService = new UserService() {
            private final List<User> list = new ArrayList<>();

            //     προσθέτουμε user se mia lista san na einai i database mas
            @Override
            public List<User> list() {
                for (int i = 0; i < 10; i++) {
                    User u = new User();
                    u.setUuid("uuid-" + i);
                    u.setUsername(String.valueOf(i));
                    u.setFirstName("firstName-" + i);
                    u.setLastName("lastName-" + i);
                    u.setDob(LocalDate.of(i, 1, 1));
                    u.setHeight(i);
                    list.add(u);
                }

                return list;
            }

            @Override
            public void add(User user) {
                list.add(user);
            }

            @Override
            public void delete(String uuid) {
                list.remove(get(uuid));
            }

            @Override
            public User get(String uuid) {
                List<User> users = list();
                return users.stream().filter(user -> user.getUuid().equals(uuid)).findFirst().orElse(null);
            }

            @Override
            public void update(User aUser) {
                for (User user : list()) {
                    if (user.getUuid().equals(aUser.getUuid())) {
                        user.setFirstName(aUser.getFirstName());
                        user.setLastName(aUser.getLastName());
                        user.setDob(aUser.getDob());
                        user.setHeight(aUser.getHeight());
                    }
                }
            }

            @Override
            public Optional<User> findByFirstName(String name) {
                //ich bringe die Liste
                List<User> users = list();
                //ich suche die Liste und gebe zurück das Ergebnis.
                return users.stream().filter(user -> user.getFirstName().equals(name)).findFirst();
            }

            @Override
            public Optional<User> findByUsername(String name) {

                List<User> users = list();
                return users.stream().filter(user -> user.getUsername().equals(name)).findFirst();
            }
        };


        controller = new UserController(userService, roleService);

        model = new ConcurrentModel();

    }

    @Test
    void showTheIndexSite() {

        var site = indexController.home();

        assertNotNull(site);
        assertEquals("index", site);
    }

    @Test
    void callTheUserFormulaToCreateNewUser() {
        var site = controller.createObject(model);
        assertNotNull(site);
        assertEquals("user/userFormula", site);

        var user = model.getAttribute("user");
        assertNotNull(user);
        assertTrue(user instanceof User);
        assertNull(((User) user).getUuid());
        assertNull(((User) user).getFirstName());
        assertNull(((User) user).getLastName());
        assertNull(((User) user).getDob());
        assertEquals(0, ((User) user).getHeight());
    }

    @Test
    void usersWithNoneRoleSeeTheUserList() {
        var site = controller.getList(model, principal);
        assertNotNull(site);
        assertEquals("error/ErrorNotAuthorized", site);
    }

    @Test
    void usersWithAdminRoleSeeTheUserList() {

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");
        var site = controller.getList(model, mockPrincipal);

        assertNotNull(site);
        assertEquals("user/userList", site);

        var users = model.getAttribute("users");
        assertNotNull(users);
        assertTrue(users instanceof List<?>);
        assertEquals(10, ((List<?>) users).size());
    }

    @Test
    void updateUserWithAdminRole() {

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");

        var updateSite = controller.updateObject("uuid-1", model, mockPrincipal);

        var user = model.getAttribute("user");

        //That is important step
        assertTrue(user instanceof User);
        assertEquals(((User) user).getUuid(), "uuid-1");
        assertEquals(((User) user).getFirstName(), "firstName-1");
        assertEquals(((User) user).getLastName(), "lastName-1");
        assertEquals(((User) user).getHeight(), 1);
        assertNotNull(updateSite);
        assertEquals("user/userFormula", updateSite);
    }

    @Test
    void updateObjectNullUuid() {
        var updateSiteNull = controller.updateObject("", model, principal);

        assertNotNull(updateSiteNull);
        assertEquals("redirect:/user/list", updateSiteNull);
    }

    @Test
    void updateObjectNoneRole() {

        var mockPrincipal = getMockPrincipal("ROLE_NONe");
        var update = controller.updateObject("uuid-1", model, mockPrincipal);

        assertNotNull(update);
        assertEquals("redirect:/user/list", update);
    }

    @Test
    void deleteObjectAdminRole() {

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");
        var delete = controller.deleteObject("uuid-1", mockPrincipal);

        assertNotNull(delete);
        assertEquals("redirect:/user/list", delete);
    }

    @Test
    void deleteObjectNoRole() {

        var mockPrincipal = getMockPrincipal("ROLE_NONe");
        var delete = controller.deleteObject("uuid-1", mockPrincipal);
        assertNotNull(delete);
        assertEquals("error/ErrorNotAuthorized", delete);
    }

    @Test
    void postObjectNoneRole() {

        //create the parameters for the method
        User newUser = getNewUser();
        //set uuid empty, that means the user don't exit and we add him
        newUser.setUuid("");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");
        var mockPrincipal = getMockPrincipal("ROLE_NONE");
        var model = new ConcurrentModel();

        // call the method
        String site = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        //check the results
        assertNotNull(site);
        assertEquals("success/SuccessfullyAdded", site);
    }

    @Test
    void postObjectWithBindingErrors() {
        //create the parameters for the method
        User newUser = getNewUser();

        //create an error in the binding results
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        var model = new ConcurrentModel();

        // call the method
        String site = controller.postObject(newUser, bindingResult, model, principal);
        //check the results
        assertNotNull(site);
        assertEquals("user/userFormula", site);
    }

    @Test
    void postObjectWrongDayOfBirth() {
        //create the parameters for the method
        User newUser = getNewUser();
        newUser.setDob(LocalDate.of(1119, 5, 5));

        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        var model = new ConcurrentModel();

        // call the method
        String site = controller.postObject(newUser, bindingResult, model, principal);

        //check the results
        assertNotNull(site);
        assertEquals("user/userFormula", site);
    }

    @Test
    void postObjectAdminRoleUpdateUser() {

        //create the parameters for the method
        User newUser = getNewUser();
        newUser.setUuid("uuid-1");

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        var model = new ConcurrentModel();

        // call the method
        String site = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        //check the results
        assertNotNull(site);
        assertEquals("redirect:/user/list", site);
    }

    @Test
    void postObjectAdminRoleAddUser() {

        //create the parameters for the method
        User newUser = getNewUser();
        newUser.setUuid("");

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        var model = new ConcurrentModel();

        // call the method
        String site = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        //check the results
        assertNotNull(site);
        assertEquals("redirect:/user/list", site);
    }

    @Test
    void postObjectNoLoginUpdateUser() {
        //someone without login try to update a user just sending the url to the server!
        //but we are thought about that, and we are prepared
        var newUser = getNewUser();
        var mockPrincipal = getMockPrincipal("ROLE_NONE");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        // call the method
        String responseSite = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        assertNotNull(responseSite);
        assertEquals("error/ErrorNotAuthorized", responseSite);
    }

    @Test
    void postObjectWithUserRoleAddNewUser() {

        var newUser = getNewUser();
        // add new user means until now he has no uuid!
        newUser.setUuid("");

        var mockPrincipal = getMockPrincipal("ROLE_USER");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        String responseSite = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        assertNotNull(responseSite);
        assertEquals("success/SuccessfullyAdded", responseSite);
    }

    @Test
    void postObjectWithUserRoleUpdateHisOwnData() {
        var newUser = getNewUser();
        //we have user form 1-10 in our test database!
        newUser.setUsername("1");

        var mockPrincipal = getMockPrincipal("ROLE_USER");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");
        // mockup that the login username is 1 like the one in the user object...
        when(mockPrincipal.getName()).thenReturn("1");

        String responseSite = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        assertNotNull(responseSite);
        assertEquals("success/SuccessfullyAdded", responseSite);
    }

    @Test
    void postObjectWithUserRoleUpdateOtherData() {

        var newUser = getNewUser();
        var mockPrincipal = getMockPrincipal("ROLE_USER");
        var bindingResult = new DirectFieldBindingResult(newUser, "user");

        String responseSite = controller.postObject(newUser, bindingResult, model, mockPrincipal);

        assertNotNull(responseSite);
        assertEquals("error/ErrorNotAuthorized", responseSite);
    }


    @Test
    void seeOnlyYourDataUserRolePositiv() {
        var mockPrincipal = getMockPrincipal("ROLE_USER");
        when(mockPrincipal.getName()).thenReturn("1");

        String responseSite = controller.readObject(model, mockPrincipal);

        assertEquals("user/userFormula", responseSite);
    }

    @Test
    void seeOnlyYourDataNoneRole() {
        var mockPrincipal = getMockPrincipal("NONE_ROLE");
        when(mockPrincipal.getName()).thenReturn("1");

        String responseSite = controller.readObject(model, mockPrincipal);

        assertEquals("error/ErrorNotAuthorized", responseSite);
    }

    @Test
    void seeOnlyYourDataNoUser() {
        var mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("asdf");

        String responseSite = controller.readObject(model, mockPrincipal);

        assertEquals("index", responseSite);
    }


    private static UsernamePasswordAuthenticationToken getMockPrincipal(String role) {

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken mockPrincipal = mock(UsernamePasswordAuthenticationToken.class);
        when(mockPrincipal.getAuthorities()).thenReturn(authorities);

        return mockPrincipal;
    }

}