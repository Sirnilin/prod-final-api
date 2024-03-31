package com.example.prodolymp.controllers;

import com.example.prodolymp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam(name = "error", required = false) String error, @RequestParam("login") String login, @RequestParam String password){
        System.out.println("test-1");
        if(error != null){
            System.out.println(error);
        }

        String storedPassword = userService.getStoredPassword(login);
        System.out.println(login + " " + password);
        if(storedPassword == null || !passwordEncoder.matches(password, storedPassword)){
            System.out.println("invalid data");
            return "login";
        }

        return "redirect:/admin";
    }
}
