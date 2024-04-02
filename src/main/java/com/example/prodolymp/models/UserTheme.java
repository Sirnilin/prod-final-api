package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "user_theme")
public class UserTheme {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "theme_id")
    private  Long theme_id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

}
