package com.example.prodolymp.controllers;

import com.example.prodolymp.models.*;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.repositories.ThemesRepositories;
import com.example.prodolymp.service.ImageService;
import com.example.prodolymp.service.ThemesService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemesController {
    private final ThemesService themesService;
    private final TokenService tokenService;
    private final ImageService imageService;

    @Operation(summary = "Получить все темы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Темы получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/")
    public ResponseEntity<Object> getAllThemes(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    List<ThemesModel> themesList = themesService.getAllThemes(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(themesList);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Создать новый курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курс создан", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("/new")
    private ResponseEntity<Object> createNewTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания новой темы",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"title\": \"Название темы\",\n" +
                                            "  \"category\": \"Категория темы\",\n" +
                                            "  \"description\": \"Описание темы\",\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request) {
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        System.out.println(user.get().getRole());
                        if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                            String title = (String) request.get("title");
                            String category = (String) request.get("category");
                            String description = (String) request.get("description");

                            ThemesModel theme = themesService.createTheme(title, category, description, user.get());

                            if(theme == null){
                                reason.setReason("Bad title, category, description, author");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(theme);
                        }else{
                            reason.setReason("The user is not an administrator");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                        }
                    }else {
                        reason.setReason("Error when receiving the user profile");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                    }
                }else {
                    reason.setReason("Invalid date body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Получить все темы пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/me")
    public ResponseEntity<Object> getAllUserThemes(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Set<ThemesModel> themesList = themesService.getAllUserTheme(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(themesList);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Подписка на курс по айди")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно подписан на курс", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/add/{id}")
    public ResponseEntity<Object> subscribeToTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @PathVariable Long id){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(themesService.subscribeToTheme(id, user.get())){
                        UserModel currentUser = user.get();
                        currentUser.setPassword(null);
                        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
                    }
                    reason.setReason("Вы уже подписаны на этот курс, или такого курса не существует!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Задача выполнена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно прошел задачу", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/complete/{themeId}")
    public ResponseEntity<Object> completeTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Айди задачи, которую он прошел", required = true, example = "43", schema = @Schema(type = "43"))
            @PathVariable Long themeId){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(themesService.completeTask(themeId, user.get())){
                        UserModel currentUser = user.get();
                        currentUser.setPassword(null);
                        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
                    }
                    reason.setReason("Вы уже выполнили эту задачу, такой задачи не существует или вы не подписаны на тему!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Получить пост по айди")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/getBy/{id}")
    public ResponseEntity<Object> getThemeById(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Айди темы, которую нужно получить", required = true, example = "43", schema = @Schema(type = "integer"))
            @PathVariable Long id){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    ThemesModel theme = themesService.getThemeById(id);
                    if(theme != null){
                        return ResponseEntity.status(HttpStatus.OK).body(theme);
                    }
                    reason.setReason("Курс с таким айди не существует");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
    @Operation(summary = "Создать подтему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подтема создана", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnderThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("/createUnderTheme")
    public ResponseEntity<Object> createUnderTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для получения поста",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"title\": \"Название подтемы\",\n" +
                                            "  \"id\": \"айди темы к которой добавить\",\n" +
                                            "  \"video_url\": \"Ссылка на видео\",\n" +
                                            "  \"description\": \"Описание темы\",\n" +
                                            "  \"points\": \"Количество поинтовайди(число, а не строка)\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                            Long id = ((Integer) request.get("id")).longValue();
                            String title = (String) request.get("title");
                            String url = (String) request.get("video_url");
                            String description = (String) request.get("description");
                            Integer points = (Integer) request.get("points");

                            UnderThemesModel under = themesService.createUnderThemes(id, title, description, url, points);

                            if(under == null){
                                reason.setReason("Bad request");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(under);
                        }else{
                            reason.setReason("The user is not an administrator");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                        }
                    }else {
                        reason.setReason("Error when receiving the user profile");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                    }
                }else {
                    reason.setReason("Invalid date body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
    @Operation(summary = "Создать таску для подтемы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "таска создана", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnderThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("/createTask")
    public ResponseEntity<Object> createTask(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для получения поста",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"id\": \"айди темы к которой добавить\",\n" +
                                            "  \"response\": \"ответ на задачу\",\n" +
                                            "  \"description\": \"Описание задачи\",\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                            Long id = ((Integer) request.get("id")).longValue();
                            String response = (String) request.get("response");
                            String description = (String) request.get("description");


                            TaskModel task = themesService.createTask(description, response, id);

                            if(task == null){
                                reason.setReason("Bad request");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(task);
                        }else{
                            reason.setReason("The user is not an administrator");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                        }
                    }else {
                        reason.setReason("Error when receiving the user profile");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                    }
                }else {
                    reason.setReason("Invalid date body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
    @Operation(summary = "Добавить фото для подзадачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "фото добавлено(возвращается ссылка на фотку)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("addImageUnderTheme/{underThemeId}")
    public ResponseEntity<Object> addImageUnderTheme(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Файл изображения", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID подтемы", required = true, example = "123", schema = @Schema(type = "integer"))
            @PathVariable Long underThemeId){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                        String image = imageService.saveImage(file);
                        UnderThemesModel under = themesService.addImageUnderTheme(image, underThemeId);
                        if(under != null){
                            return ResponseEntity.status(HttpStatus.OK).body(under);
                        }
                        reason.setReason("Такой подтемы не существует");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
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
    @Operation(summary = "Добавить фото для задачки подзадачи")
    @PostMapping("addImageTask/{taskId}")
    public ResponseEntity<Object> addImageTask(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Файл изображения", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID задачи", required = true, example = "123", schema = @Schema(type = "integer"))
            @PathVariable Long taskId){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                        String image = imageService.saveImage(file);
                        TaskModel task = themesService.addImageTask(image, taskId);
                        if(task != null){
                            return ResponseEntity.status(HttpStatus.OK).body(task);
                        }
                        reason.setReason("Такой таски не существует");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
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
