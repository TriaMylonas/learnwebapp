package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndexControllerTest extends AbstractApplicationTests {

    private IndexController controller;

    private Model model;

    private Principal principal;

    @BeforeEach
    void setUp() {
        // κάναμε to UserService interface kai me auto ton tropo prepei na dosoume zoi se oles tou tis methodous.
        controller = new IndexController(new UserService() {

            private final List<User> list = new ArrayList<>();

            //     προσθέτουμε user se mia lista san na einai i database mas
            @Override
            public List<User> list() {
                for (int i = 0; i < 10; i++) {
                    User u = new User();
                    u.setUuid("uuid-" + i);
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
                for (User user : list()) {
                    if (user.getUuid().equals(uuid)) {
                        return user;
                    }
                }
                return null;
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
        });

        model = new ConcurrentModel();

    }

    @Test
    void startSite() {

        var site = controller.startSite();

        assertNotNull(site);
        assertEquals("index", site);
    }

    @Test
    void formulaForNewUser() {
        var site = controller.formulaForNewUser(model);
        assertNotNull(site);
        assertEquals("formula", site);

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
    void users() {
        var site = controller.users(model,principal);
        assertNotNull(site);
        assertEquals("user", site);

        var users = model.getAttribute("users");
        assertNotNull(users);
        assertTrue(users instanceof List<?>);
        assertEquals(10, ((List<?>) users).size());
    }

    @Test
    void positiveAdminRoleUpdate(){
        /*  principal instanceof UsernamePasswordAuthenticationToken. Principal ist eine Interface, deswegen mache ich
            eine Objekt von UsernamePasswordAuthenticationToke und gebe die Parameter die ich möchte.
            https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/authentication/UsernamePasswordAuthenticationToken.html
            Konstruktor = UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities)
         */
        // Define the user's authorities or roles


        // Create a UsernamePasswordAuthenticationToken with your principal, credentials, and authorities
        principal = new UsernamePasswordAuthenticationToken(principal, "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        var mockPrincipal = getMockPrincipal("ROLE_ADMIN");

        var updateSite = controller.update("uuid-1", model, mockPrincipal);

        var user = model.getAttribute("user");

        //That is important step
        assertTrue(user instanceof User);
        assertEquals(((User) user).getUuid(), "uuid-1");
        assertEquals(((User) user).getFirstName(), "firstName-1");
        assertEquals(((User) user).getLastName(), "lastName-1");
        assertEquals(((User) user).getHeight(), 1);
        assertNotNull(updateSite);
        assertEquals("formula", updateSite);
    }

    private static UsernamePasswordAuthenticationToken getMockPrincipal(String role) {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken mockPrincipal = mock(UsernamePasswordAuthenticationToken.class);
        when(mockPrincipal.getAuthorities()).thenReturn(authorities);
        return mockPrincipal;
    }

    @Test
    void negativeUserNullUpdate(){
        var updateSiteNull = controller.update("", model, principal);

        assertNotNull(updateSiteNull);
        assertEquals("redirect:/users", updateSiteNull);
    }

    @Test
    void positiveNoRoleUpdate(){
        // I give the user no role
        principal = new UsernamePasswordAuthenticationToken(principal, "password", List.of(new SimpleGrantedAuthority("NONE")));

        var update = controller.update("uuid-1",model,principal);
        assertNotNull(update);
        assertEquals("redirect:/users", update);
    }

    @Test
    void deleteAdminRole() {

        principal = new UsernamePasswordAuthenticationToken(principal, "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        var delete = controller.delete("uuid-1", principal);

        assertNotNull(delete);
        assertEquals("redirect:/users", delete);
    }

    @Test
    void deleteNoRole(){
        var delete = controller.delete("uuid-1", principal);
        assertNotNull(delete);
        assertEquals("redirect:/error/ErrorNotAuthorized", delete);
    }
    @Test
    void registerSitePositive() {

        //create the parameters for the method
        User newUser = new User();
        newUser.setUuid("uuid-test");
        newUser.setFirstName("testFirstName");
        newUser.setLastName("testLastName");
        newUser.setDob(LocalDate.of(1999, 5, 5));
        newUser.setHeight(185);

        var bindingResult = new DirectFieldBindingResult(null, "");

        var model = new ConcurrentModel();


        // call the method
        String site = controller.registerSite(newUser, bindingResult, model, principal);
        //check the results
        assertNotNull(site);
        assertEquals("redirect:/users", site);
    }

    @Test
    void registerSiteNegative(){
        //create the parameters for the method
        User newUser = new User();
        newUser.setUuid("uuid-test");
        newUser.setFirstName("testFirstName");
        newUser.setLastName("testLastName");
        newUser.setDob(LocalDate.of(1999, 5, 5));
        newUser.setHeight(185);

        //create an error in the binding results
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        var model = new ConcurrentModel();

        // call the method
        String site = controller.registerSite(newUser, bindingResult, model, principal);
        //check the results
        assertNotNull(site);
        assertEquals("formula", site);
    }
}