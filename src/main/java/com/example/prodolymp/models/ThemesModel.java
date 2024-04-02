package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Data
@Entity
@Table(name = "themes")
public class ThemesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 50)
    @Schema(description = "Заголовок курса. Максимальная длина - 50 символолв.")
    private String title;

    @Column(name = "category", length = 50)
    @Schema(description = "Категория курса. Максимальная длина - 50 символов.")
    private String category;

    @Column(name = "description", length = 300)
    @Schema(description = "Описание курса. Максимальная длина - 300 символов.")
    private String description;

    @Column(name = "explored")
    @Schema(description = "Пройдена или не пройдена задача.")
    private Boolean explored;

    @Column(name = "started")
    @Schema(description = "Поступил или не поступил")
    private Boolean started;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id")
    private AuthorModel author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "theme_under", joinColumns = @JoinColumn(name = "theme_id"), inverseJoinColumns = @JoinColumn(name = "under_id"))
    private Set<UnderThemesModel> under= new HashSet<>();

    @Column(name = "points")
    @Schema(description = "Кол-во очков, начисляемое за курс")
    private Integer points;

    @Column(name = "students")
    @Schema(description = "Кол-во студентов на курсе.")
    private Integer students;

    @Column(name = "graduates.")
    @Schema(description = "Кол-во завершивишх курс")
    private Integer graduates;

    @Column(name = "grade")
    @Schema(description = "Оценка пользователей этой задачи")
    private Float grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonIgnore
    private UserModel user;

}
