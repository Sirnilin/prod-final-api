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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                    List<ThemesModel> themesList = themesService.getAllThemes();
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
                                            "  \"author\": \"Автор темы\",\n" +
                                            "  \"points\": \"Количество поинтовайди(число, а не строка)\"\n" +
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
                        if(user.get().getRole() == Role.ROLE_USER){
                            String title = (String) request.get("title");
                            String category = (String) request.get("category");
                            String description = (String) request.get("description");
                            String author = (String) request.get("author");
                            Integer points = (Integer) request.get("points");

                            ThemesModel theme = themesService.createTheme(title, category, description, author, points);

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
                    List<ThemesModel> themesList = themesService.getAllUserTheme(user.get());
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
    @PostMapping("/add")
    public ResponseEntity<Object> subscribeToTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для подписки на пост",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"id\": \"айди(число, а не строка)\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Long id = ((Integer) request.get("id")).longValue();
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

    @Operation(summary = "Курс пройден")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно прошел курс", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/complete")
    public ResponseEntity<Object> completeTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для прохождения курса",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"id\": \"айди(число, а не строка)\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Long id = ((Integer) request.get("id")).longValue();
                    if(themesService.completeTheme(id, user.get())){
                        UserModel currentUser = user.get();
                        currentUser.setPassword(null);
                        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
                    }
                    reason.setReason("Вы уже прошли этот курс, или такого курса не существует!");
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

    @Operation(summary = "Получить все категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все категории получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/category")
    public ResponseEntity<Object> getAllCategory(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    List<String> categoryList = themesService.getAllCategory();
                    return ResponseEntity.status(HttpStatus.OK).body(categoryList);
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
    @GetMapping("/getById")
    public ResponseEntity<Object> getThemeById(
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
                                            "  \"id\": \"айди(число, а не строка)\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Long id = ((Integer) request.get("id")).longValue();
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
            @Parameter(description = "Файл изображения подтемы", required = true)
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
                                            "  \"video_url\": \"Ссылка на видео\",\n" +
                                            "  \"description\": \"Описание темы\",\n" +
                                            "  \"points\": \"Количество поинтовайди(число, а не строка)\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestPart("file") MultipartFile file,
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        if(user.get().getRole() == Role.ROLE_USER){
                            Long id = ((Integer) request.get("id")).longValue();
                            String title = (String) request.get("title");
                            String url = (String) request.get("video_url");
                            String description = (String) request.get("description");
                            Integer points = (Integer) request.get("points");

                            String image = imageService.saveImage(file);

                            UnderThemesModel under = themesService.createUnderThemes(id, title, description, url, image, points);

                            if(under == null){
                                reason.setReason("Bad request");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(image);
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

    @PostMapping("/createTask")
    public ResponseEntity<Object> createTask(
            @RequestHeader("Authorization") String token,
            @RequestPart("file") MultipartFile file,
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        if(user.get().getRole() == Role.ROLE_USER){
                            Long id = ((Integer) request.get("id")).longValue();
                            String response = (String) request.get("response");
                            String description = (String) request.get("description");
                            String image = imageService.saveImage(file);

                            TaskModel task = themesService.createTask(description, response, image, id);

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
}
