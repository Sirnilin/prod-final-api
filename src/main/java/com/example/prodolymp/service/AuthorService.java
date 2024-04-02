package com.example.prodolymp.service;

import com.example.prodolymp.models.AuthorModel;
import com.example.prodolymp.models.ThemesModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.repositories.AuthorRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepositories authorRepositories;

    public Set<ThemesModel> getAllAuthorTheme(UserModel user){
        return authorRepositories.findByUserId(user.getId()).getThemes();
    }

    public AuthorModel setAuthorInfo(String name, String description, UserModel user){
        AuthorModel author = authorRepositories.findByUserId(user.getId());

        if(author == null){
            return null;
        }

        author.setName(name);
        author.setDescription(description);

        authorRepositories.save(author);

        return author;
    }

    public AuthorModel getAuthorByUser(UserModel user){
        return authorRepositories.findByUserId(user.getId());
    }
}
