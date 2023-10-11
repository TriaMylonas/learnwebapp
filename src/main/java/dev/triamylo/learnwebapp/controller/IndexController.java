package dev.triamylo.learnwebapp.controller;


import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
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
    public String formulaForNewUser(Model model){

        // I initialise one object, so that the form in the html can bind with it.
        User user = new User();

        // with "user" obj I have bind my form through Thymeleaf to.
        model.addAttribute("user", user);
        return "formula";
    }


    @GetMapping("/users")
    public String users(Model model) {

        List<User> users = userService.list();

        model.addAttribute("users", users);
        return "user";
    }


    @GetMapping("/users/update/{uuid}")
    public  String update(@PathVariable String uuid, Model model){

        User user = userService.get(uuid);
        if (user != null){
            model.addAttribute("user", user);
            return "formula";
        }

        return "redirect:/users";
    }

    @GetMapping("/users/delete/{uuid}")
    public String delete(@PathVariable String uuid) {

        userService.delete(uuid);
        //with redirect, will refresh the page users!
        return "redirect:/users";
    }



    /* with post method I take values from the client to server side.
     * here I take the values that the client put in the form on formula.html
     * when he clicks Submit in the form the values will go to the /register url
     * and I will then start the registerSite() method from this controller.
     * if there is a need of a logic I will be writing it in a Service class and
     * call it from this method.
     */
    @PostMapping("/formula")
    public String registerSite(@Valid @ModelAttribute("user") User aUser, BindingResult bindingResult) {


        // here in the Controller I can also validate the connection between my objects and the model.
        if (aUser.getDob() != null) {
            // that is the value that will come from the form.
            int test = aUser.getDob().getYear();

            if (test < 1980 || test > 2000) {
                //I create am Error to display in the model after
                bindingResult.rejectValue("dob", "error.dob.notValidRange", "Date range is not valid");
            }
        }

        //checking for errors from the Annotations in the Model. If there is something wrong and the values
        // from the form can not agree with my model, will initialise BindingResult object.
        if (bindingResult.hasErrors()) {
            return "formula";
        }

        //that means that is a new user without ID until now.
        if( aUser.getUuid() == null || aUser.getUuid().isEmpty()){
            //I add the new user after the validation to my list of users
            userService.add(aUser);
        }
        else{

            userService.update(aUser);
        }

        //redirect to refresh the page.
        return "redirect:/users";

    }


}
