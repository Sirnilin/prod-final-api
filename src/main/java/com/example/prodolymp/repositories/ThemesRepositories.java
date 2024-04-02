package com.example.prodolymp.repositories;

import com.example.prodolymp.models.ThemesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ThemesRepositories extends JpaRepository<ThemesModel, Long> {
}
