package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import jakarta.persistence.*;


@Data
@Entity
@Table(name = "lesson_model")
public class LessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "explored")
    private Boolean explored;
}
