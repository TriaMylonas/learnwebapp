package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.AbstractMockUpTests;
import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class RoleControllerMockMvcTest extends AbstractMockUpTests {


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        //clean database before each testing
        roleRepository.deleteAll();

        //Prepare roles in the database for the testing
        Role role1 = getNewRole("role1", "description1");
        roleRepository.save(role1);
        Role role2 = getNewRole("role2", "description2");
        roleRepository.save(role2);
        Role role3 = getNewRole("role3", "description3");
        roleRepository.save(role3);
    }

    @AfterEach
    void cleanUp(){
        //clean database after all test because they are stay in the DB and they disturb other tests
        roleRepository.deleteAll();
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createObjectRoleWithAdminRolePositive() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("role/roleFormula"))
                .andExpect(model().attributeExists("role"));
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void createObjectRoleWithNoRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void readObjectRoleWithAdminRolePositive() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/read"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/roleList"));
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void readObjectRoleWithNoRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/read"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateObjectWithAdminRolePositive() throws Exception {

        String uuid = roleRepository.findByRoleName("role1").get().getUuid();

        mockMvc.perform(MockMvcRequestBuilders.get("/role/update/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("role/roleFormula"))
                .andExpect(model().attributeExists("role"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateObjectRoleWithAdminRoleObjectNull() throws Exception {

        String uuid = "roleDontExist";

        mockMvc.perform(MockMvcRequestBuilders.get("/role/update/{uuid}", uuid))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/roleList"))
                .andExpect(model().attributeDoesNotExist("role"));
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void updateObjectRoleWithNoRole() throws Exception {

        String uuid = roleRepository.findByRoleName("role1").get().getUuid();

        mockMvc.perform(MockMvcRequestBuilders.get("/role/update/{uuid}", uuid))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteObjectRoleWithAdminRolePositiv() throws Exception {

        String uuid = roleRepository.findByRoleName("role2").get().getUuid();
        ;

        mockMvc.perform(MockMvcRequestBuilders.get("/role/delete/{uuid}", uuid))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/list"));

        Assertions.assertTrue(roleRepository.findByRoleName("role1").isPresent());
        Assertions.assertFalse(roleRepository.findByRoleName("role2").isPresent());
        Assertions.assertTrue(roleRepository.findByRoleName("role3").isPresent());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteObjectRoleWithAdminRoleWrongUuid() throws Exception {

        String uuid = "wrongUuid";

        mockMvc.perform(MockMvcRequestBuilders.get("/role/delete/{uuid}", uuid))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/list"));
        //nothing in the testDB deleted
        Assertions.assertTrue(roleRepository.findByRoleName("role1").isPresent());
        Assertions.assertTrue(roleRepository.findByRoleName("role2").isPresent());
        Assertions.assertTrue(roleRepository.findByRoleName("role3").isPresent());
    }


    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void deleteObjectRoleWithNoRole() throws Exception {

        String uuid = "NoRole";

        mockMvc.perform(MockMvcRequestBuilders.get("/role/update/{uuid}", uuid))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getListRolesWithAdminRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("role/roleList"))
                .andExpect(model().attributeExists("roles"));

        //I will take again the model to see if the list is inside
        ModelMap modelMap = (ModelMap) mockMvc.perform(MockMvcRequestBuilders.get("/role/list"))
                .andReturn()
                .getModelAndView()
                .getModel();

        Assertions.assertFalse(modelMap.isEmpty());

        //Save the model to a list, so I can check the values of the list.
        List<Role> roles = (List<Role>) modelMap.get("roles");
        Assertions.assertFalse(roles.isEmpty());
        Assertions.assertNotNull(roles);
        Assertions.assertEquals(3, roles.size());
        Assertions.assertEquals("role1", roles.get(0).getRoleName());
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void getListRolesWithNoRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/role/list"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectRoleWithAdminRoleAddNewRolePositive() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("roleName", "testRole")
                        .param("roleDescription", "testDescription")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/list"));

        Assertions.assertNotNull(roleRepository.findByRoleName("testRole"));
        Assertions.assertEquals(roleRepository.findByRoleName("testRole").get().getRoleDescription(),
                "testDescription");

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectRoleWithAdminRoleAddNewRoleWithBindingErrors() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("roleName", "1")
                        .param("roleDescription", "testDescription1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("role/roleFormula"));

        Assertions.assertFalse(roleRepository.findByRoleName("1").isPresent());
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void postObjectRoleWithNoRoleAddNewRole() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("roleName", "testRole")
                        .param("roleDescription", "testDescription")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Assertions.assertFalse(roleRepository.findByRoleName("testRole").isPresent());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectRoleWithAdminRoleUpdateRolePositive() throws Exception {

        Optional<Role> optionalRole = roleRepository.findByRoleName("role1");
        Assertions.assertTrue(optionalRole.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("uuid", optionalRole.get().getUuid() )
                        .param("roleName", "role1")
                        .param("roleDescription", "testDescription1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/role/list"));

        Role role = roleRepository.findByRoleName("role1").get();
        Assertions.assertNotNull(role);
        Assertions.assertEquals(role.getRoleDescription(),"testDescription1");

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postObjectRoleWithAdminRoleUpdateRoleWithBindingErrors() throws Exception {

        Optional<Role> optionalRole = roleRepository.findByRoleName("role1");
        Assertions.assertTrue(optionalRole.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("uuid", optionalRole.get().getUuid() )
                        .param("roleName", "1")
                        .param("roleDescription", "testDescription1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("role/roleFormula"));
        //the description is not change because the roleName hat a validation problem
        Assertions.assertNotEquals(optionalRole.get().getRoleDescription(),"testDescription1");
    }

    @Test
    @WithMockUser(roles = "NO_ADMIN")
    void postObjectRoleWithNoRoleUpdateRole() throws Exception {

        Optional<Role> optionalRole = roleRepository.findByRoleName("role1");
        Assertions.assertTrue(optionalRole.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/role/post")
                        .param("uuid", optionalRole.get().getUuid() )
                        .param("roleName", "role1")
                        .param("roleDescription", "testDescription1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        Role role = roleRepository.findByRoleName("role1").get();
        Assertions.assertNotNull(role);
        Assertions.assertEquals(role.getRoleDescription(),"description1");
    }







}
