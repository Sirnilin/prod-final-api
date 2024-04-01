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

    public int createUser(String phone, String password, String firstname, String surname, String lastname, Boolean admin){
        if(phone == null || password == null || firstname == null || surname == null || lastname == null){
            return 1;
        }

        if(!isUnique(phone)){
            return 2;
        }

        if(!isPasswordValid(password)){
            return 1;
        }

        if(!isPhoneValid(phone)){
            return 1;
        }

        UserModel user = new UserModel();
        user.setPhone(phone);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setSurname(surname);
        user.setPoints(0);
        user.setPassword(passwordEncoder.encode(password));

        if(admin){
            user.setRole(Role.ROLE_ADMIN);
        }else{
            user.setRole(Role.ROLE_USER);
        }
        userRepositories.save(user);

        return 0;
    }

    private Boolean isUnique(String phone){
        return userRepositories.findByPhone(phone) == null;
    }

    private boolean isPasswordValid(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return passwordMatcher.matches();
    }

    private boolean isPhoneValid(String phone) {
        return phone.matches("\\+\\d+") && phone.length() <= 20;
    }

    public UserModel getUserByPhone(String phone){
        return userRepositories.findByPhone(phone);
    }

    public String getStoredPassword(String phone){
        UserModel user = userRepositories.findByPhone(phone);
        return user.getPassword();
    }
}
