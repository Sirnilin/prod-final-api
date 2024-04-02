package com.example.prodolymp.repositories;

import com.example.prodolymp.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositories extends JpaRepository<ProductModel, Long> {
}
