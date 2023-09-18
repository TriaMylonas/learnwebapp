package dev.triamylo.learnwebapp.Controllers;


import dev.triamylo.learnwebapp.Models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {


    @GetMapping("/")
    public String startSite(Model model) {
        User user = new User("", "");
        model.addAttribute("user", user);
        return "index";
    }

//    @GetMapping("/")

    @PostMapping("/register")
    public String registerSite(@ModelAttribute("user") User aUser, Model model) {

//        model.addAttribute("User",aUser);
        return "register";
    }


}
