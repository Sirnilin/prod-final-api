package com.example.prodolymp.repositories;

import com.example.prodolymp.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepositories extends JpaRepository<CategoryModel, Long> {
}
