package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class RoleController extends AbstractController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("/rolesList")
    public String showListWithRoles(Model model, Principal principal) {

        if (hasAdminRole(principal)) {
            List<Role> roles = roleService.list();
            model.addAttribute("roles", roles);
            return "roleList";
        }

        return "error/ErrorNotAuthorized";
    }

}
