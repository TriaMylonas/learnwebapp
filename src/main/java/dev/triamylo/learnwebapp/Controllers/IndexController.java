package dev.triamylo.learnwebapp.Controllers;


import dev.triamylo.learnwebapp.Models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {


    /*get method is when the client ask for the current url and I must (server side) give him
    * something back. When it is a Controller I must give the client html view back. When it is a
    * RestController I give back Object as JSON in the regel.
    */
    @GetMapping("/")
    public String startSite(Model model) {
        // I initialise one object, so that the form in the html can bind with it.
        User user = new User("tria", "myl");
        //I pass it to the model (html) through model and attribute name "user"
        // with "user" obj I have bind my form through Thymeleaf to.
        model.addAttribute("user", user);

        //when the client ask for the root (localhost:8080) I give him bak the index.html view
        // the spring boot search for this view in the resource/templates/ folder as default.
        return "index";
    }


    /* with post method I take values from the client to server side.
    * here I take the values that the client put in the form on index.html
    * when he clicks Submit in the form the values will go to the /register url
    * and I will then start the registerSite() method from this controller.
    * if there is a need of a logic I will be writing it in a Service class and
    * call it from this method.
    */
    @PostMapping("/register")
    public String registerSite(@ModelAttribute("user") User aUser, Model model) {
        // here I take the values from the html and put them in the obj user.
        model.addAttribute("User",aUser);
        return "register";
    }


}
