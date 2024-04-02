package com.example.prodolymp.controllers;

import com.example.prodolymp.models.*;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.service.AuthorService;
import com.example.prodolymp.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;
    private final TokenService tokenService;

    @Operation(summary = "Все темы автора получены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Темы получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/themes")
    public ResponseEntity<Object> getAllUserThemes(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                        Set<ThemesModel> themes = authorService.getAllAuthorTheme(user.get());
                        return ResponseEntity.status(HttpStatus.OK).body(themes);
                    }else{
                        reason.setReason("The user is not an administrator");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                    }
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Изменить данные автора")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль автора успешно изменен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))})
    @PatchMapping("/setAuthor")
    public ResponseEntity<Object> setAuthorInf(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class), examples = {
                            @ExampleObject(name = "example_request", value = "{\"name\": \"String\", \"description\": \"String\"}")
                    })
            )
            @RequestBody HashMap<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    String name = (String) request.get("name");
                    String description = (String) request.get("description");

                    AuthorModel author = authorService.setAuthorInfo(name, description, user.get());

                    return ResponseEntity.ok(author);
                } else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Получить профиль автора")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль автора получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/getAuthor")
    public ResponseEntity<Object> getAuthor(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                        AuthorModel author = authorService.getAuthorByUser(user.get());
                        return ResponseEntity.status(HttpStatus.OK).body(author);
                    }else{
                        reason.setReason("The user is not an administrator");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                    }
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
}
