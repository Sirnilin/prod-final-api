package com.example.prodolymp.service;

import com.example.prodolymp.models.*;
import com.example.prodolymp.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ThemesService {
    private final ThemesRepositories themesRepositories;
    private final UnderThemesRepositories underThemesRepositories;
    private final TaskRepositories taskRepositories;
    private final UserRepositories userRepositories;
    private final AuthorRepositories authorRepositories;

    public List<ThemesModel> getAllThemes(UserModel user){
        List<ThemesModel> themesModelList = themesRepositories.findAll();
        /*List<ThemesModel> result = new ArrayList<>();
        for(ThemesModel theme : themesModelList){
            if(user.getCompleteThemeIds().contains(theme.getId())){
                theme.setStarted(true);
            }else{
                theme.setStarted(false);
            }
            result.add(theme);
        }*/

        return themesModelList;
    }

    public ThemesModel createTheme(String title, String category, String description, UserModel user){
        if(title.length() > 50){
            return null;
        }

        if(category.length() > 50){
            return null;
        }

        if(description.length() > 300){
            return null;
        }
        AuthorModel author = authorRepositories.findByUserId(user.getId());

        if(author == null){
            author = new AuthorModel();

            author.setUserId(user.getId());
            author.setName(user.getFirstname());
            author.setDescription("");
            authorRepositories.save(author);
        }

        ThemesModel theme = new ThemesModel();

        theme.setTitle(title);
        theme.setCategory(category);
        theme.setDescription(description);
        theme.setGraduates(0);
        theme.setPoints(0);
        theme.setStudents(0);
        theme.setExplored(false);
        theme.setAuthor(author);

        author.getThemes().add(theme);

        themesRepositories.save(theme);
        authorRepositories.save(author);

        return theme;
    }

    public Set<ThemesModel> getAllUserTheme(UserModel user){

        return user.getThemes();
    }

    public Boolean subscribeToTheme(Long id, UserModel user){
        if(themesRepositories.findById(id).isEmpty() || user.getThemes().contains(themesRepositories.findById(id).get())){
            return false;
        }
        ThemesModel theme = themesRepositories.findById(id).get();
        theme.setStudents(theme.getStudents() + 1);
        theme.setStarted(true);
        user.getThemes().add(theme);

        userRepositories.save(user);
        return true;
    }

    public Boolean completeTask(Long id, UserModel user){
        TaskModel task = taskRepositories.findById(id).get();

        if(taskRepositories.findById(id).isEmpty() || user.getThemes().contains(task.getUnder().getTheme())){
            return false;
        }

        task.setExplored(true);

        UnderThemesModel under = task.getUnder();
        Boolean flag1 = true;

        for(TaskModel tempTask : under.getTasks()){
            if (!tempTask.getExplored()) {
                flag1 = false;
                break;
            }
        }

        under.setExplored(flag1);

        ThemesModel theme = under.getTheme();
        Boolean flag2 = true;

        for(UnderThemesModel tempUnder : theme.getUnder()){
            if(!tempUnder.getExplored()){
                flag2 = false;
                break;
            }
        }

        theme.setExplored(flag2);

        taskRepositories.save(task);
        underThemesRepositories.save(under);
        themesRepositories.save(theme);
        return true;
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

        under.setTheme(theme);

        underThemesRepositories.save(under);

        theme.getUnder().add(under);
        theme.setPoints(theme.getPoints() + points);
        themesRepositories.save(theme);

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

        task.setUnder(under);

        taskRepositories.save(task);

        under.getTasks().add(task);
        underThemesRepositories.save(under);
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

