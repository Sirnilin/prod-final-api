package com.example.prodolymp.controllers;

import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.service.TokenService;
import com.example.prodolymp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestHeader("Authorization") String token){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    return ResponseEntity.ok(user.get());
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("reason", "Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("reason", "Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
