package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "under_themes")
public class UnderThemesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @Column(name = "discription")
    private String description;

    @Column(name = "explored")
    private Boolean explored;

    @ElementCollection
    @CollectionTable(name = "underTemes_tasks", joinColumns = @JoinColumn(name = "underThemes_id"))
    @Column(name = "tasks_id")
    private Set<Long> tasksIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "underTemes_lesson", joinColumns = @JoinColumn(name = "underThemes_id"))
    @Column(name = "lesson_id")
    private Set<Long> lessonIds = new HashSet<>();

}
