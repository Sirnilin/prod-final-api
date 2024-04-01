package com.example.prodolymp.repositories;

import com.example.prodolymp.models.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepositories extends JpaRepository<TaskModel, Long> {
}
