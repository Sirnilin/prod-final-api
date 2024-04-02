package com.example.prodolymp.repositories;

import com.example.prodolymp.models.AuthorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepositories extends JpaRepository<AuthorModel, Long> {
    AuthorModel findByUserId(Long id);
}
