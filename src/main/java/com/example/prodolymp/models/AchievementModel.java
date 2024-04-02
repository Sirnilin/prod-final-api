package com.example.prodolymp.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "achievement")
public class AchievementModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    @Schema(description = "Описание достижения")
    private String description;

    @Column(name = "is_completed")
    @Schema(description = "Прогресс выполнения достижения")
    private Boolean isCompleted;

    @Column(name = "image")
    private String image;
}
