package com.example.prodolymp.controllers;

import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.UserRepositories;
import com.example.prodolymp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserControlller {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, Object> request) {
        if (request == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Request body is missing");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        System.out.println(request);
        String login = (String) request.get("login");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        int resultCode = userService.createUser(login, email, password);
        if (resultCode == 0) {
            System.out.println("User registered successfully");
            UserModel user = new UserModel();
            Map<String, Object> response = new HashMap<>();
            user.setPassword(password);
            user.setEmail(email);
            user.setLogin(login);
            response.put("profile", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
