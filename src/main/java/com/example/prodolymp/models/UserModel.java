package com.example.prodolymp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password", length = 1000)
    private String password;
}
