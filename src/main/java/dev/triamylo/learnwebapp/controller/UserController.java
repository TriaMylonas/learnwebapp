package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.RoleServiceImp;
import dev.triamylo.learnwebapp.service.UserService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController extends AbstractController {
    private static final int MIN_DOB_YEAR = 1980;
    private static final int MAX_DOB_YEAR = 2000;

    private final UserService userService;

    private final RoleServiceImp roleService;

    public UserController(UserService userService, RoleServiceImp roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    ;


    @GetMapping("/user/create")
    public String createObject(Model model) {

        // I initialise one object, so that the form in the html can bind with it.
        User user = new User();

        // with "user" obj I have bind my form through Thymeleaf to.
        model.addAttribute("user", user);
        addDoBRanges(model);
        return "user/userFormula";
    }


    @GetMapping("/user/read")
    public String readObject(Model model, Principal principal) {
        String username = principal.getName();

        Optional<User> optionalUser = userService.findByUsername(username);
        User user;

        if (optionalUser.isPresent()) {
            user = (User) optionalUser.get();

            //if the user is "ROLE_USER" (just a user) and has the same username as the login user, he can update only his stats
            if (hasUserRole(principal) && user.getUsername().equals(username)) {

                model.addAttribute("user", user);
                addDoBRanges(model);
                return "user/userFormula";
            }
            return "error/ErrorNotAuthorized";
        }
        return "index";
    }


    @GetMapping("/user/update/{uuid}")
    public String updateObject(@PathVariable String uuid, Model model, Principal principal) {
        User user = userService.get(uuid);

        if (user != null) {

            List<Role> roleList = rolesThatTheUserDoNotHave(user);

            //if the user is admin, he can do with all the user.
            if (hasAdminRole(principal)) {
                model.addAttribute("user", user);
                model.addAttribute("roles", roleList); // Add the roles to the model
                addDoBRanges(model);
                return "user/userFormula";
            }
        }
        return "redirect:/user/list";
    }


    @GetMapping("/user/delete/{uuid}")
    public String deleteObject(@PathVariable String uuid, Principal principal) {
        if (hasAdminRole(principal)) {

            userService.delete(uuid);
            //with redirect, will refresh the page users!
            return "redirect:/user/list";
        }
        return "error/ErrorNotAuthorized";

    }


    @GetMapping("/user/list")
    public String getList(Model model, Principal principal) {
        List<User> users = userService.list();
        if (hasAdminRole(principal)) {
            model.addAttribute("users", users);
            return "user/userList";
        }
        return "error/ErrorNotAuthorized";
    }


    /* with post method I take values from the client to server side.
     * here I take the values that the client put in the form on userFormula.html
     * when he clicks Submit in the form the values will go to the /register url
     * and I will then start the registerSite() method from this controller.
     * if there is a need of a logic I will be writing it in a Service class and
     * call it from this method.
     */

    @PostMapping("/user/post")
    public String postObject(@Valid @ModelAttribute("user") User aUser, BindingResult bindingResult, Model model, Principal principal) {

        // here in the Controller I can also validate the connection between my objects and the model.
        if (aUser.getDob() != null) {
            // that is the value that will come from the form.
            int test = aUser.getDob().getYear();

            if (test < MIN_DOB_YEAR || test > MAX_DOB_YEAR) {
                //I create am Error to display in the model after
                bindingResult.rejectValue("dob", "error.dob.notValidRange", "Date range is not valid");
            }
        }

        //checking for errors from the Annotations in the Model. If there is something wrong and the values
        // from the form can not agree with my model, will initialise BindingResult object.
        if (bindingResult.hasErrors()) {
            addDoBRanges(model);
            return "user/userFormula";
        }

        // Admin->
        if (hasAdminRole(principal)) {

            //that means that is a new user without ID until now.
            if (aUser.getUuid() == null || aUser.getUuid().isEmpty()) {
                //I add the new user after the validation to my list of users
                userService.add(aUser);
            } else {

                userService.update(aUser);
            }
            return "redirect:/user/list";
        }

        //User ->
        if (hasUserRole(principal)) {
            //that means that is a new user without ID until now.
            if (aUser.getUuid() == null || aUser.getUuid().isEmpty()) {
                userService.add(aUser);
                return "success/SuccessfullyAdded";
            }
            //that means the username of the object is same with the username login
            else if (aUser.getUsername().equals(principal.getName())) {
                userService.update(aUser);
                return "success/SuccessfullyAdded";
            } else {
                //don't authorize
                return "error/ErrorNotAuthorized";
            }
        }

        //No Login ->
        if (aUser.getUuid() == null || aUser.getUuid().isEmpty()) {
            //I add the new user after the validation to my list of users
            userService.add(aUser);
            return "success/SuccessfullyAdded";
        }

        return "error/ErrorNotAuthorized";
    }


    @PostMapping("user/addRole/{uuid}")
    public String postUserAddRole(@PathVariable String uuid, Model model){
        Role newRole = roleService.get(uuid);

        String test =(String) model.getAttribute("usersUuid");

        User user = userService.get(test);

        user.addRole(newRole);

        return "redirect:/user/update/{uuid}";
    }




    //    extra prüfung, dass die Daten die ich von der Form bekomme, sind richtig.

    private void addDoBRanges(Model model) {
        model.addAttribute("dobMin", MIN_DOB_YEAR + "-01-01");
        model.addAttribute("dobMax", MAX_DOB_YEAR + "-01-01");
    }

    private List<Role> rolesThatTheUserDoNotHave(User user) {
        List<Role> allAvailableRoles = roleService.list();
        List<Role> userRoles = user.getRoles();

        return allAvailableRoles.stream().filter(role -> !userRoles.contains(role))
                .collect(Collectors.toList());
    }


}
