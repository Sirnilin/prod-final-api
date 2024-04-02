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

    @Column(name = "started")
    @Schema(description = "Подтема начата.")
    private Boolean started;

    @Column(name = "image", length = 1000)
    @Schema(description = "Ссылка на картинку для подзадачи.")
    private String image;

    @Column(name = "grade")
    @Schema(description = "Оценка пользователей этой подзадачию")
    private Float grade;

    @Column(name = "points")
    @Schema(description = "Кол-во баллов, начисляемое за выполенние этой задачи.")
    private Integer points;

    @Column(name = "count_grade")
    @JsonIgnore
    private Integer countGrade;

    @Column(name = "sum_grade")
    @JsonIgnore
    private Integer sumGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false, referencedColumnName = "id")
    @Schema(description = "Тема этой под темы")
    @JsonIgnore
    private ThemesModel theme;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "under_task", joinColumns = @JoinColumn(name = "under_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
    @Schema(description = "Задачи в подтеме")
    private Set<TaskModel> tasks = new HashSet<>();
}
