package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @OneToMany(mappedBy = "author", targetEntity = ThemesModel.class,
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<ThemesModel> themes = new HashSet<>();

}
