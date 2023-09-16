package dev.triamylo.learnwebapp;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(){

        JustAGuy obj1 = new JustAGuy("tria","Mylo",25);

        return obj1.toString();
    }


    @GetMapping("/")
    public String startSite(){
        return "Hello in my first web app!";
    }


}
