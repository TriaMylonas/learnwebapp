package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RoleController extends AbstractController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("/role/create")
    public String createObject(Model model) {

        Role role = new Role();
        model.addAttribute("role", role);
        return "role/roleFormula";
    }


    @GetMapping("/role/read")
    public String readObject() {

        //I don't need that controller. I have just write it for the name convention.
        return "redirect:/role/roleList";
    }


    @GetMapping("/role/update/{uuid}")
    public String updateObject(@PathVariable String uuid, Model model) {
        Role role = roleService.get(uuid);

        return role == null ?
                "redirect:/role/roleList" : renderRoleForm(model, role);
    }

    @GetMapping("/role/delete/{uuid}")
    public String deleteObject(@PathVariable String uuid) {
        Role role = roleService.get(uuid);
        if (role != null) {
            detachRoleFromUsers(role);
            roleService.delete(uuid);
        }
        return "redirect:/role/list";
    }

    @GetMapping("/role/list")
    public String getList(Model model) {
        List<Role> roles = roleService.list();
        model.addAttribute("roles", roles);
        return "role/roleList";

    }


    @PostMapping("/role/post")
    public String postObject(@Valid @ModelAttribute("role") Role role, BindingResult bindingResult) {
        return bindingResult.hasErrors() ?
                "role/roleFormula" : processRoleSubmission(role);
    }

    private void detachRoleFromUsers(Role role) {
        List<User> userWithThisRole = role.getUsers();
        for (User user : userWithThisRole) {
            user.getRoles().remove(role);
        }
        userWithThisRole.clear();
        roleService.update(role);
    }

    private String processRoleSubmission(Role role) {
        roleService.update(role);
        return "redirect:/role/list";
    }

    private String renderRoleForm(Model model, Role role) {
        model.addAttribute("role", role);
        return "role/roleFormula";
    }
}