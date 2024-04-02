package com.example.prodolymp.service;

import com.example.prodolymp.models.AchievementModel;
import com.example.prodolymp.models.UnderThemesModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.repositories.AchievementRepositories;
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
    private final AchievementRepositories achievementRepositories;

    public int createUser(String phone, String password, String firstname, String surname, String lastname, Boolean admin){
        if(phone == null || password == null || firstname == null){
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

        AchievementModel achievement1 = new AchievementModel();
        AchievementModel achievement2 = new AchievementModel();
        AchievementModel achievement3 = new AchievementModel();

        achievement1.setUser(user);
        achievement1.setName("По люБВИ");
        achievement1.setDescription("Взять ВсОШ");
        achievement1.setIsCompleted(false);
        achievement1.setImage("http://82.97.241.151:8081/images/Patent.svg");

        achievement2.setUser(user);
        achievement2.setName("Образовака");
        achievement2.setDescription("Завершить курс");
        achievement2.setIsCompleted(false);
        achievement2.setImage("http://82.97.241.151:8081/images/Medal.svg");

        achievement3.setUser(user);
        achievement3.setName("Первый");
        achievement3.setDescription("Зарегестрируйся");
        achievement3.setIsCompleted(true);
        achievement3.setImage("http://82.97.241.151:8081/images/Square_academic_cap.svg");

        achievementRepositories.save(achievement1);
        achievementRepositories.save(achievement2);
        achievementRepositories.save(achievement3);

        user.getAchievement().add(achievement1);
        user.getAchievement().add(achievement2);
        user.getAchievement().add(achievement3);
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

    public UserModel updateUserInfo(String firstname, String surname, String lastname, UserModel user){
        if(firstname != null){
            user.setFirstname(firstname);
        }

        if(surname != null){
            user.setSurname(surname);
        }

        if(lastname != null){
            user.setLastname(lastname);
        }

        userRepositories.save(user);
        return user;
    }

    public UserModel updateImage(String image, UserModel user){
        user.setImage(image);

        userRepositories.save(user);

        return user;
    }
}
