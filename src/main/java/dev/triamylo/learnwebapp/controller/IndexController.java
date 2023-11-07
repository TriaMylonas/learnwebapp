package dev.triamylo.learnwebapp.controller;


import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Controller
public class IndexController {

    private static final int MIN_DOB_YEAR = 1980;
    private static final int MAX_DOB_YEAR = 2000;

    private final UserService userService;

    public IndexController(@Autowired UserService userService) {
        this.userService = userService;
    }

    /*get method is when the client ask for the current url and I must (server side) give him something back.
     * When it is a @Controller I must give the client html view back.
     * When it is a @RestController I give back Object as JSON in the regel.
     */
    @GetMapping("/")
    public String startSite() {
        return "index";
    }


    @GetMapping("/formula")
    public String formulaForNewUser(Model model) {

        // I initialise one object, so that the form in the html can bind with it.
        User user = new User();

        // with "user" obj I have bind my form through Thymeleaf to.
        model.addAttribute("user", user);
        addDoBRanges(model);
        return "formula";
    }


    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.list();

        model.addAttribute("users", users);
        return "user";
    }


    // TODO --> test in den Controller

    @GetMapping("/users/update/{uuid}")
    public String update(@PathVariable String uuid, Model model) {

        User user = userService.get(uuid);
        if (user != null) {
            model.addAttribute("user", user);
            addDoBRanges(model);
            return "formula";
        }

        return "redirect:/users";
    }

    @GetMapping("/users/delete/{uuid}")
    public String delete(@PathVariable String uuid, Principal principal) {

        /* Trainings reasons!
         * that's not necessary because I only allow admin access to delete in the securityConfig!
         *   .requestMatchers("/users/delete/**").hasRole("ADMIN") // die Liste kann von ADMIN ausgerufen und bearbeiten werden.
         *  and to redirect to my custom error site I used this:
         * .exceptionHandling((exceptionHandling) -> exceptionHandling.accessDeniedPage("error/ErrorNotAuthorized"));
         */
        if (isUserAdmin(principal)) {
            userService.delete(uuid);
            //with redirect, will refresh the page users!
            return "redirect:/users";
        } else
            return "redirect:error/ErrorNotAuthorized";
    }


    /* with post method I take values from the client to server side.
     * here I take the values that the client put in the form on formula.html
     * when he clicks Submit in the form the values will go to the /register url
     * and I will then start the registerSite() method from this controller.
     * if there is a need of a logic I will be writing it in a Service class and
     * call it from this method.
     */
    @PostMapping("/formula")
    public String registerSite(@Valid @ModelAttribute("user") User aUser, BindingResult bindingResult, Model model, Principal principal) {

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
            return "formula";
        }

        //that means that is a new user without ID until now.
        if (aUser.getUuid() == null || aUser.getUuid().isEmpty()) {
            //I add the new user after the validation to my list of users
            userService.add(aUser);
        } else {

            userService.update(aUser);
        }

        //redirect to refresh the page.
        return "redirect:/users";

    }

    //    extra prüfung, dass die Daten die ich von der Form bekomme, sind richtig.
    private void addDoBRanges(Model model) {
        model.addAttribute("dobMin", MIN_DOB_YEAR + "-01-01");
        model.addAttribute("dobMax", MAX_DOB_YEAR + "-01-01");
    }


    // check if the login user is Admin
    private boolean isUserAdmin(Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken user) {

            Collection<GrantedAuthority> authorities = user.getAuthorities();

            for (GrantedAuthority role : authorities) {
                if (role.toString().equals("ROLE_ADMIN")) {
                    return true;
                }
            }
        }

        return false;

    }

}
