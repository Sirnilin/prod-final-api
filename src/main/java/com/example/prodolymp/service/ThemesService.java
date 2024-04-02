package com.example.prodolymp.service;

import com.example.prodolymp.models.*;
import com.example.prodolymp.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.HashSet;
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

        List<ThemesModel> result = new ArrayList<>();

        for(ThemesModel theme : themesModelList){
            theme.setStarted(user.getStartedThemeIds().contains(theme.getId()));
            theme.setExplored(user.getCompleteThemeIds().contains(theme.getId()));

            for(UnderThemesModel under : theme.getUnder()){
                under.setStarted(user.getStartedUnderThemeIds().contains(under.getId()));
                under.setExplored(user.getCompleteUnderThemeIds().contains(under.getId()));

                for (TaskModel task : under.getTasks()){
                    task.setStarted(user.getStartedTaskIds().contains(task.getId()));
                    task.setExplored(user.getCompleteTaskIds().contains(task.getId()));
                }
            }

            result.add(theme);
        }


        return result;
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
        theme.setStarted(false);
        theme.setAuthor(author);

        author.getThemes().add(theme);

        themesRepositories.save(theme);
        authorRepositories.save(author);

        return theme;
    }

    public Set<ThemesModel> getAllUserTheme(UserModel user){
        Set<ThemesModel> themesModelList = user.getThemes();

        Set<ThemesModel> result = new HashSet<>();

        for(ThemesModel theme : themesModelList){
            theme.setStarted(user.getStartedThemeIds().contains(theme.getId()));
            theme.setExplored(user.getCompleteThemeIds().contains(theme.getId()));

            for(UnderThemesModel under : theme.getUnder()){
                under.setStarted(user.getStartedUnderThemeIds().contains(under.getId()));
                under.setExplored(user.getCompleteUnderThemeIds().contains(under.getId()));

                for (TaskModel task : under.getTasks()){
                    task.setStarted(user.getStartedTaskIds().contains(task.getId()));
                    task.setExplored(user.getCompleteTaskIds().contains(task.getId()));
                }
            }

            result.add(theme);
        }


        return result;
    }

    public Boolean subscribeToTheme(Long id, UserModel user, String value){
        switch (value){
            case ("theme"):
                if(themesRepositories.findById(id).isEmpty()){
                    return false;
                }
                user.getStartedThemeIds().add(id);
                user.getThemes().add(themesRepositories.findById(id).get());
                userRepositories.save(user);
                return true;
            case ("undertheme"):
                if(underThemesRepositories.findById(id).isEmpty()){
                    return false;
                }
                user.getStartedUnderThemeIds().add(id);
                user.getThemes().add(underThemesRepositories.findById(id).get().getTheme());
                userRepositories.save(user);
                return true;
            case ("task"):
                if(taskRepositories.findById(id).isEmpty()){
                    return false;
                }
                user.getStartedTaskIds().add(id);
                user.getThemes().add(taskRepositories.findById(id).get().getUnder().getTheme());

                userRepositories.save(user);
                return true;
        }

        return false;
    }

    public Boolean completeTask(Long id, UserModel user){
        TaskModel task = taskRepositories.findById(id).get();

        if(taskRepositories.findById(id).isEmpty() || user.getThemes().contains(task.getUnder().getTheme())){
            return false;
        }

        user.getCompleteTaskIds().add(task.getId());

        UnderThemesModel under = task.getUnder();
        Boolean flag1 = true;

        for(TaskModel tempTask : under.getTasks()){
            if (!user.getCompleteTaskIds().contains(tempTask.getId())) {
                flag1 = false;
                break;
            }
        }

        if(flag1){
            user.getCompleteUnderThemeIds().add(under.getId());
        }

        ThemesModel theme = under.getTheme();
        Boolean flag2 = true;

        for(UnderThemesModel tempUnder : theme.getUnder()){
            if(!user.getCompleteUnderThemeIds().contains(tempUnder.getId())){
                flag2 = false;
                break;
            }
        }

       if(flag2){
           user.getCompleteThemeIds().add(theme.getId());
       }

        userRepositories.save(user);
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
        under.setStarted(false);

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

        task.setStarted(false);
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

