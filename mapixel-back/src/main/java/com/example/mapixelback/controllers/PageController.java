package com.example.mapixelback.controllers;


import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.jwt.JwtUtil;
import com.example.mapixelback.model.User;
import com.example.mapixelback.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class PageController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    @GetMapping("/main-page")
    public String index(Model model, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        logger.info("incoming GET request at /main-page");
        User user = userService.findUserByEmail(jwtUtil.extractUsernameFromToken(token.replace("Bearer ", "")));
        if(user!=null){
            model.addAttribute("username", user.getUsername());
            model.addAttribute("hasMaps", user.getMaps().size() != 0);
            return "index";
        }
        throw new ResourceNotFoundException("can't acces page - user doesn't exist or you don't have the necessary permission");
    }
}