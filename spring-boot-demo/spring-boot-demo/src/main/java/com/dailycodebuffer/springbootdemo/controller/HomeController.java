package com.dailycodebuffer.springbootdemo.controller;

import com.dailycodebuffer.springbootdemo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller + @ResponseBody
public class HomeController {

    @RequestMapping("/")
    public String hello() {
        return "Hello World";
    }


//    @RequestMapping(value ="/user",method= RequestMethod.GET)
    @GetMapping("/user")
    public User user(){
        User user = new User();
        user.setId("1");
        user.setName("John");
        user.setEmailId("john@gmail.com");
        return  user;
    }
}
