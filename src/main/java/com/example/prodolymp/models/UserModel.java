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

    @Column(name = "phne", unique = true)
    @Schema(description = "Номер телефона пользователя. Должен начинаться на знак + и состоять только из цифр до 20 символов.")
    private String phone;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "password", length = 1000)
    @Schema(description = "Пароль пользователя. Должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и быть длиной от 6 до 20 символов.")
    private String password;

    @Column(name = "image")
    private String image;

    @Column(name = "points")
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

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
