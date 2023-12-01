package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
        if(role != null){
            model.addAttribute("role", role);
            return "role/roleFormula";
        }
        return "redirect:/role/roleList";
    }


    @GetMapping("/role/delete/{uuid}")
    public String deleteObject(@PathVariable String uuid) {

        Role role = roleService.get(uuid);
        if(role != null){
            roleService.delete(uuid);
            return "redirect:/role/roleList";
        }
        return "redirect:/role/roleList";
    }


    @GetMapping("/role/list")
    public String getList(Model model) {
//
//        List<Role> testRoles = new ArrayList<>();
//        Role role1 = new Role("normalUser");
//        role1.setUuid("123");
//        role1.setRoleDescription("Beschreibung für die erste  Role! Es ist ein normal Nutzer");
//        testRoles.add(role1);
//        Role role2 = new Role("admin");
//        role2.setRoleDescription("Diese ist der Admin und er kann macht was er will!");
//        testRoles.add(role2);
//        Role role3 = new Role("keinRole");
//        role3.setRoleDescription("er hat keine rechte zu macht nix!");
//        testRoles.add(role3);

        List<Role> roles = roleService.list();
        model.addAttribute("roles", roles);
        return "role/roleList";

    }

    @PostMapping("/role/post")
    public String postObject(@Valid @ModelAttribute("role") Role role, BindingResult bindingResult) {

        if (!bindingResult.hasErrors()) {

            if (role.getUuid() == null || role.getUuid().isEmpty()) {
                roleService.add(role);
            } else {
                roleService.update(role);
            }
            return "redirect:/role/list";
        }

        return "role/roleFormula";
    }


}