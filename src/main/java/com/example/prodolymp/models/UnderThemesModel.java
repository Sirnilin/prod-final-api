package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Column(name = "title", length = 50)
    @Schema(description = "Заголовок подзадачи для курса. Максимальная длина - 50 символолв.")
    private String title;

    @Column(name = "discription", length = 300)
    @Schema(description = "Описание подзадачи. Максимальная длина - 300 символов.")
    private String description;

    @Column(name = "video_url", length = 1000)
    @Schema(description = "Ссылка на видео. Максимальная длина - 1000 символов.")
    private String videoUrl;

    @Column(name = "explored")
    @Schema(description = "Пройдена или не пройдена подзадача.")
    private Boolean explored;

    @Column(name = "image", length = 1000)
    @Schema(description = "Ссылка на картинку для подзадачи.")
    private String image;

    @Column(name = "grade")
    @Schema(description = "Оценка пользователей этой подзадачию")
    private Float grade;

    @Column(name = "points")
    @Schema(description = "Кол-во баллов, начисляемое за выполенние этой задачи.")
    private Integer points;

    @ElementCollection
    @CollectionTable(name = "under_themes_tasks", joinColumns = @JoinColumn(name = "under_themes_id"))
    @Column(name = "tasks_id")
    private Set<Long> tasksIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "under_themes_lesson", joinColumns = @JoinColumn(name = "under_themes_id"))
    @Column(name = "lesson_id")
    private Set<Long> lessonIds = new HashSet<>();

}
