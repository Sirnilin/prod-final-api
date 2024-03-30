package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

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

    @Schema(description = "Пароль пользователя. Должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и быть длиной от 6 до 20 символов.")
    public String getPassword() {
        return password;
    }
}
