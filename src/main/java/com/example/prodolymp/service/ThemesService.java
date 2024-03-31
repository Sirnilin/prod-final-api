package com.example.prodolymp.service;

import com.example.prodolymp.models.ThemesModel;
import com.example.prodolymp.repositories.ThemesRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemesService {
    private final ThemesRepositories themesRepositories;

    public List<ThemesModel> getAllThemes(){
        return themesRepositories.findAll();
    }

    public ThemesModel createTheme(String title, String category, String description, String author, Integer points){
        if(title.length() > 50){
            return null;
        }

        if(category.length() > 50){
            return null;
        }

        if(description.length() > 300){
            return null;
        }

        if(author.length() > 50){
            return null;
        }

        ThemesModel theme = new ThemesModel();

        theme.setTitle(title);
        theme.setCategory(category);
        theme.setDescription(description);
        theme.setAuthor(author);
        theme.setGraduates(0);
        theme.setPoints(points);
        theme.setStudents(0);

        themesRepositories.save(theme);

        return theme;
    }
}
