package dev.triamylo.learnwebapp.Controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

    @GetMapping("/hello")
    public String greetingPage(@RequestParam(defaultValue = "World",required = false) String name, Model model){
        /*here I will pass the String name that I will take from the
        *URL as parameter to the greeting.html site that I have created and connected with
        *the Thymeleaf in the Model object. Now the thymeleaf will search for attribute "name" in
        * the .html and will change the value on it. After that I tell him to return this page.
        */
        model.addAttribute("name",name);
        return "greetings";
    }

}
