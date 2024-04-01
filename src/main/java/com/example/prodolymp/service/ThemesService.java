package com.example.prodolymp.service;

import com.example.prodolymp.models.TaskModel;
import com.example.prodolymp.models.ThemesModel;
import com.example.prodolymp.models.UnderThemesModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemesService {
    private final ThemesRepositories themesRepositories;
    private final UnderThemesRepositories underThemesRepositories;
    private final TaskRepositories taskRepositories;
    private final UserRepositories userRepositories;

    public List<ThemesModel> getAllThemes(){
        return themesRepositories.findAll();
    }

    public ThemesModel createTheme(String title, String category, String description, String author, Integer points){
        System.out.println(title.length());
        System.out.println(category.length());
        System.out.println(description.length());
        System.out.println(author.length());
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

    public ThemesModel getThemeById(Long id){
        return themesRepositories.findById(id).get();
    }

    public UnderThemesModel createUnderThemes(Long id, String title, String description, String url, Integer points){
        ThemesModel theme = themesRepositories.findById(id).get();

        if(theme == null){
            return null;
        }

        UnderThemesModel under = new UnderThemesModel();

        if(description.length() > 300){
            return null;
        }

        if(url.length() > 1000){
            return null;
        }

        if(title.length() > 50){
            return null;
        }

        under.setExplored(false);
        under.setGrade((float) 0);
        under.setDescription(description);
        under.setTitle(title);
        under.setVideoUrl(url);
        under.setPoints(points);

        theme.getUnderThemeIds().add(under.getId());

        themesRepositories.save(theme);
        underThemesRepositories.save(under);

        return under;
    }

    public TaskModel createTask(String description, String response, Long id){
        UnderThemesModel under = underThemesRepositories.findById(id).get();

        if(under == null){
            return null;
        }

        if(description.length() > 300){
            return null;
        }

        if(response.length() > 300){
            return null;
        }

        TaskModel task = new TaskModel();

        task.setExplored(false);
        task.setResponse(response);
        task.setDescription(description);

        under.getTasksIds().add(task.getId());

        underThemesRepositories.save(under);
        taskRepositories.save(task);

        return task;
    }

    public UnderThemesModel addImageUnderTheme(String image, Long id){
        UnderThemesModel under = underThemesRepositories.findById(id).get();

        if(under == null){
            return under;
        }

        under.setImage(image);

        underThemesRepositories.save(under);

        return under;
    }

    public TaskModel addImageTask(String image, Long id) {
        TaskModel task = taskRepositories.findById(id).get();

        if (task == null) {
            return task;
        }

        task.setImage(image);

        return task;
    }
}

