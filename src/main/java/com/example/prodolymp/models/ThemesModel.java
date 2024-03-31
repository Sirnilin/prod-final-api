package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "description", length = 50)
    @Schema(description = "Описание курса. Максимальная длина - 300 символов.")
    private String description;

    @Column(name = "explored")
    private Boolean explored;

    @Column(name = "author", length = 50)
    @Schema(description = "Автор курса. Максимальня длина 50 символов.")
    private String author;

    @Column(name = "points")
    @Schema(description = "Кол-во очков, начисляемое за курсю")
    private Integer points;

    @Column(name = "students")
    @Schema(description = "Кол-во очков студентов на курсе.")
    private Integer students;

    @Column(name = "Кол-во тех кто закончил курс.")
    @Schema(description = "Кол-во очков студентов на курсе.")
    private Integer graduates;

    @ElementCollection
    @CollectionTable(name = "themes_underTemes", joinColumns = @JoinColumn(name = "themes_id"))
    @Column(name = "underThemes_id")
    private Set<Long> underThemeIds = new HashSet<>();
}
