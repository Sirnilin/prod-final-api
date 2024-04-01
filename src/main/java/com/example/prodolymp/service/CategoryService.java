package com.example.prodolymp.service;

import com.example.prodolymp.models.CategoryModel;
import com.example.prodolymp.repositories.CategoryRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepositories categoryRepositories;

    public List<CategoryModel> getAllCategory(){
        return categoryRepositories.findAll();
    }

    public CategoryModel addCategory(String value){
        CategoryModel category = new CategoryModel();

        category.setCategory(value);

        categoryRepositories.save(category);

        return category;
    }
}
