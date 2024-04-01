package com.example.prodolymp.repositories;

import com.example.prodolymp.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositories extends JpaRepository<UserModel, Long> {
    UserModel findByPhone(String phone);
}
