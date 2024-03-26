package com.example.prodolymp.service;

import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepositories userRepositories;

    public int createUser(String login, String password, String email){
        if(login != null || password != null || email != null){
            UserModel user = new UserModel();
            user.setLogin(login);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepositories.save(user);
            return 0;
        }else{
            return 1;
        }
    }
}
