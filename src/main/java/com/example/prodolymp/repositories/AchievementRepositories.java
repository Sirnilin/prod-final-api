package com.example.prodolymp.repositories;

import com.example.prodolymp.models.AchievementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepositories extends JpaRepository<AchievementModel, Long>{
}
