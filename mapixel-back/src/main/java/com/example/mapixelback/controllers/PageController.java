package com.example.mapixelback.controllers;


import com.example.mapixelback.model.User;
import com.example.mapixelback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    @Autowired
    private UserService userService;
    @GetMapping("/main-page/{id}")
    public String index(Model model, @PathVariable String id) {
        User user = userService.findUserById(id);
        if(user!=null){
            model.addAttribute("username", user.getUsername());
            model.addAttribute("hasMaps", user.getMaps().size() != 0);
            return "index";
        }
        else return "error404";
    }
}