package com.example.prodolymp.service;

import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepositories userRepositories;

    public int createUser(String login, String password, String email,Boolean admin){
        if(login == null || password == null || email == null){
            return 1;
        }

        if(!isUnique(email, login)){
            return 2;
        }

        if(!isPasswordValid(password)){
            return 1;
        }

        if(!isLoginValid(login)){
            return 1;
        }

        if(isEmailValid(email)){
            return 1;
        }

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));

        if(admin){
            user.getRoles().add(Role.ROLE_ADMIN);
        }else{
            user.getRoles().add(Role.ROLE_USER);
        }
        userRepositories.save(user);

        return 0;
    }

    private Boolean isUnique(String email, String login){
        return userRepositories.findByEmail(email) == null && userRepositories.findByLogin(login) == null;
    }

    private boolean isPasswordValid(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return passwordMatcher.matches();
    }

    private boolean isLoginValid(String login) {
        Pattern loginPattern = Pattern.compile("[a-zA-Z0-9-]+");
        Matcher loginMatcher = loginPattern.matcher(login);
        return loginMatcher.matches();
    }

    private boolean isEmailValid(String email){
        return email.length() > 50;
    }

    public UserModel getUserByLogin(String login){
        return userRepositories.findByLogin(login);
    }

    public String getStoredPassword(String login){
        UserModel user = userRepositories.findByLogin(login);
        return user.getPassword();
    }
}
