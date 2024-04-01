package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "task_model")
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @Column(name = "description", length = 300)
    @Schema(description = "Описание этой задачки")
    private String description;

    @Column(name = "response", length = 300)
    @Schema(description = "Ответ на задачку.")
    private String response;

    @Column(name = "explored")
    @Schema(description = "Выполнена или не выполнена.")
    private Boolean explored;

    @Column(name = "image", length = 1000)
    @Schema(description = "Картинка для задачки.")
    private String image;


}
