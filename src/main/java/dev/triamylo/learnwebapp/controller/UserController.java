package dev.triamylo.learnwebapp.controller;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.RoleService;
import dev.triamylo.learnwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController extends AbstractController {
    private static final int MIN_DOB_YEAR = 1980;
    private static final int MAX_DOB_YEAR = 2000;

    private final UserService userService;

    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


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

            List<Role> notAssignRoles = rolesThatTheUserDoNotHave(user);

            //if the user is admin, he can do with all the user.
            if (hasAdminRole(principal)) {
                model.addAttribute("user", user);
                model.addAttribute("notAssignRoles", notAssignRoles); // Add the roles to the model
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


    @PostMapping("user/{uuid}/addRole")
    public String postUserAddRole(@PathVariable String uuid, @RequestParam String roleUuid) {
        // Get the selected role and add it to the user's roles
        Role role = roleService.get(roleUuid);
        //Get the user
        User user = userService.get(uuid);
        //Add the role to the user
        user.addRole(role);
        // Update the user with the new role
        userService.update(user);

        // Redirect to the user update page
        return "redirect:/user/update/" + user.getUuid();
    }

    @PostMapping("user/{userUuid}/deleteRole/{roleUuid}")
    public String deleteRoleFromUser(@PathVariable String userUuid, @PathVariable String roleUuid) {
        // get the user from db
        User user = userService.get(userUuid);
        //get the role from db
        Role role = roleService.get(roleUuid);
        //remove the role from users roleList
        user.getRoles().remove(role);
        // update the user
        userService.update(user);

        //redirect to the user update page
        return "redirect:/user/update/" + userUuid;
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
