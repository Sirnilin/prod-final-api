package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Data
@Entity
@Table(name = "author_model")
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AuthorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    @Schema(description = "Айди пользователя, который стал этим автором.")
    private Long userId;

    @Column(name = "name")
    @Schema(description = "Имя автора")
    private String name;

    @Column(name = "description")
    @Schema(description = "Описание профиля автора")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "author_theme", joinColumns = @JoinColumn(name = "author_id"), inverseJoinColumns = @JoinColumn(name = "theme_id"))
    @JsonIgnore
    private Set<ThemesModel> themes = new HashSet<>();

}
