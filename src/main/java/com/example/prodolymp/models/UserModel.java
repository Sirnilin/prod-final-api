package com.example.prodolymp.models;

import com.example.prodolymp.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;


import lombok.Data;


import jakarta.persistence.*;
import org.springframework.ui.context.Theme;

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

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AchievementModel> achievement = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "user_theme", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "theme_id"))
    private Set<ThemesModel> themes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_complete_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "theme_id")
    private Set<Long> completeThemeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_complete_under_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "under_theme_id")
    private Set<Long> completeUnderThemeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_complete_task", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "task_id")
    private Set<Long> completeTaskIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_started_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "theme_id")
    private Set<Long> startedThemeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_started_under_themes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "under_theme_id")
    private Set<Long> startedUnderThemeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_started_task", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "task_id")
    private Set<Long> startedTaskIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_product_id", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "product_id")
    private Set<Long> productIds = new HashSet<>();

    @Schema(description = "Пароль пользователя. Должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и быть длиной от 6 до 20 символов.")
    public String getPassword() {
        return password;
    }

}
