package com.example.prodolymp.service;

import com.example.prodolymp.models.ThemesModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.ThemesRepositories;
import com.example.prodolymp.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemesService {
    private final ThemesRepositories themesRepositories;
    private final UserRepositories userRepositories;

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
        theme.setExplored(false);

        themesRepositories.save(theme);

        return theme;
    }

    public List<ThemesModel> getAllUserTheme(UserModel user){
        List<ThemesModel> result = new ArrayList<>();

        for(Long id : user.getThemeIds()){
            ThemesModel theme = themesRepositories.findById(id).get();
            if(user.getCompleteThemeIds().contains(id)){
                theme.setExplored(true);
            }else{
                theme.setExplored(false);
            }
            result.add(theme);
        }
        return result;
    }

    public Boolean subscribeToTheme(Long id, UserModel user){
        if(themesRepositories.findById(id).isEmpty() || user.getThemeIds().contains(id)){
            return false;
        }

        user.getThemeIds().add(id);

        userRepositories.save(user);
        return true;
    }

    public Boolean completeTheme(Long id, UserModel user){
        if(themesRepositories.findById(id).isEmpty() || user.getCompleteThemeIds().contains(id)){
            return false;
        }

        user.getCompleteThemeIds().add(id);

        userRepositories.save(user);
        return true;
    }

    public List<String> getAllCategory(){
        List<String> result = new ArrayList<>();
        List<ThemesModel> themes = themesRepositories.findAll();

        for(ThemesModel theme: themes){
            if(!result.contains(theme.getCategory())){
                result.add(theme.getCategory());
            }
        }

        return result;
    }
}
