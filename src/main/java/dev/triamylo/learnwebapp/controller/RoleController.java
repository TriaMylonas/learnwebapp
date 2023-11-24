package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RoleController extends AbstractController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }




    @GetMapping("/rolesList")
    public String getList(Model model,
                                    Principal principal) {

        List<Role> testRoles = new ArrayList<>();
        Role role1 = new Role("normalUser");
        role1.setUuid("123");
        role1.setRoleDescription("Beschreibung für die erste  Role! Es ist ein normal Nutzer");
        testRoles.add(role1);
        Role role2 = new Role("admin");
        role2.setRoleDescription("Diese ist der Admin und er kann macht was er will!");
        testRoles.add(role2);
        Role role3 = new Role("keinRole");
        role3.setRoleDescription("er hat keine rechte zu macht nix!");
        testRoles.add(role3);

        if (hasAdminRole(principal)) {
            List<Role> roles = roleService.list();
            model.addAttribute("roles", testRoles);
            return "roleList";
        }

        return "error/ErrorNotAuthorized";
    }

    @GetMapping("/roleFormula")
    public String getFormula(Model model) {

        Role role = new Role();
        model.addAttribute("role", role);
        return "roleFormula";
    }


    @PostMapping("/addRole")
    public String postFormula(@Valid @ModelAttribute("role") Role role,
                                     BindingResult bindingResult,
                                     Principal principal) {

        if (!hasAdminRole(principal)) {
            return "error/ErrorNotAuthorized";
        }
        else {

            if(!bindingResult.hasErrors()){

                if(role.getUuid() == null || role.getUuid().isEmpty()){
                    roleService.add(role);
                }
                else {
                    roleService.update(role);
                }
                return "success/SuccessfullyAdded";
            }
            return "redirect:/roleFormula";
        }
    }

//    @GetMapping("/role/update/{uuid}")
//    @GetMapping("/role/delete/{uuid}"
//    create
//    read
}
