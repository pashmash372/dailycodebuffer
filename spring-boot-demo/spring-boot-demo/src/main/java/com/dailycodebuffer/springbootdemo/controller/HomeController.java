package com.dailycodebuffer.springbootdemo.controller;

import com.dailycodebuffer.springbootdemo.model.User;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/{id2}")
    public String pathVariable(@PathVariable("id") String id
            ,@PathVariable("id2") String id2){
        return "PathVariable is : " + id +":"+id2;
    }
}
