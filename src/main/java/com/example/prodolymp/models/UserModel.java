package com.example.prodolymp.models;

import com.example.prodolymp.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;


import lombok.Data;


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @Column(name = "login", unique = true)
    @Schema(description = "Логин пользователя. Должен состоять из букв латинского алфавита (в верхнем или нижнем регистре), цифр и дефиса. Длина от 3 до 20 символов.")
    private String login;

    @Column(name = "email", unique = true)
    @Schema(description = "Email пользователя. Максимальная длина - 50 символов.")
    private String email;

    @Column(name = "password", length = 1000)
    @Schema(description = "Пароль пользователя. Должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и быть длиной от 6 до 20 символов.")
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "theme_id")
    private Set<Long> themeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_complete_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "theme_id")
    private Set<Long> completeThemeIds = new HashSet<>();

    @Schema(description = "Пароль пользователя. Должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и быть длиной от 6 до 20 символов.")
    public String getPassword() {
        return password;
    }

}
