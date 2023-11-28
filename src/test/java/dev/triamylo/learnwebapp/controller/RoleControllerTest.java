package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractApplicationTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleControllerTest extends AbstractApplicationTests {

    private Model model;

    private RoleController roleController;

    //    Ich erstelle meine eigene userService und generiert eine Liste mit Roles für meine UnitTests
    @BeforeEach
    void setUp() {

        model = new ConcurrentModel();

        roleController = new RoleController(new RoleService() {
            private final List<Role> roles = new ArrayList<>();

            @Override
            public List<Role> list() {
                roles.add(getNewRole("1", "role1", "description1"));
                roles.add(getNewRole("2", "role2", "description2"));
                roles.add(getNewRole("3", "role3", "description3"));

                return roles;
            }

            @Override
            public void add(Role role) {
                list().add(role);
            }

            @Override
            public void delete(String uuid) {
                list().remove(get(uuid));
            }

            @Override
            public Role get(String uuid) {
                for (Role role : list()) {
                    if (role.getUuid().equals(uuid)) {
                        return role;
                    }
                }
                return null;
            }

            @Override
            public void update(Role newRole) {

                for (Role role : list()) {
                    if (role.getUuid().equals(newRole.getUuid())) {
                        role.setRoleName(newRole.getRoleName());
                        role.setRoleDescription(newRole.getRoleDescription());
                    }
                }
            }

        });
    }

    @Test
    void createObjectRole() {

        var responseSite = roleController.createObject(model);
        assertNotNull(responseSite);
        assertEquals("role/roleFormula", responseSite);

        var role = model.getAttribute("role");
        assertNotNull(role);
        assertTrue(role instanceof Role);
        assertNull(((Role) role).getRoleName());
        assertNull(((Role) role).getRoleDescription());
    }

    @Test
    void readObjectRole(){
        var responseSite = roleController.readObject();
        assertNotNull(responseSite);
        assertEquals("redirect:/role/roleList", responseSite);
    }

    @Test
    void updateObjectRolePositive(){

        var responseSite = roleController.updateObject("1",model);
        assertNotNull(responseSite);
        assertEquals("role/roleFormula", responseSite);

        var role = model.getAttribute("role");
        assertNotNull(role);
        assertTrue(role instanceof Role);
        assertEquals(((Role) role).getRoleName(),"role1" );
        assertEquals(((Role) role).getRoleDescription(), "description1");
    }

    @Test
    void updateObjectRoleWennRoleDontExist(){

        var responseSite = roleController.updateObject("dontExist", model);
        assertNotNull(responseSite);
        assertEquals("redirect:/role/roleList", responseSite);
    }

    @Test
    void deleteObjectRolePositive(){

        var responseSite = roleController.deleteObject("1");
        assertNotNull(responseSite);
        assertEquals("redirect:/role/roleList", responseSite);
    }

    @Test
    void deleteObjectRoleWennRoleDontExist(){

        var responseSite = roleController.deleteObject("dontExist");
        assertNotNull(responseSite);
        assertEquals("redirect:/role/roleList",responseSite);
    }

    @Test
    void getListRolesPositive(){

        var responseSite = roleController.getList(model);
        assertNotNull(responseSite);
        assertEquals("role/roleList", responseSite);

        var rolesList = model.getAttribute("roles");
        assertNotNull(rolesList);
        assertTrue(rolesList instanceof List<?>);
        assertEquals(3, ((List<?>) rolesList).size());
    }

    @Test
    void postObjectRolePositiveNewRoleAdd(){

        Role testRole = new Role();
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        var responseSite = roleController.postObject(testRole,bindingResult);
        assertNotNull(responseSite);
        assertEquals("success/SuccessfullyAdded", responseSite);
    }

    @Test
    void postObjectRolePositiveExistingRoleUpdate(){

        Role testRole = getNewRole();
        testRole.setUuid("1"); //existing in the test lsit
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        var responseSite = roleController.postObject(testRole,bindingResult);
        assertNotNull(responseSite);
        assertEquals("success/SuccessfullyAdded", responseSite);
    }

    @Test
    void postObjectRoleWithBindingResult(){

        Role testRole = new Role();
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        var responseSite = roleController.postObject(testRole,bindingResult);
        assertNotNull(responseSite);
        assertEquals("redirect:role/roleFormula", responseSite);
    }





















}