package com.example.prodolymp.repositories;

import com.example.prodolymp.models.UnderThemesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnderThemesRepositories extends JpaRepository<UnderThemesModel, Long> {
}
