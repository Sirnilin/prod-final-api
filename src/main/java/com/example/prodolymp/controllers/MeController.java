package com.example.prodolymp.controllers;

import com.example.prodolymp.models.ReasonModel;
import com.example.prodolymp.models.TaskModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.service.ImageService;
import com.example.prodolymp.service.TokenService;
import com.example.prodolymp.service.UserService;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final TokenService tokenService;
    private final UserService userService;
    private final ImageService imageService;
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен профиль пользователя", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))})
    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    return ResponseEntity.ok(user.get());
                } else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Изменить данные пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль пользователя успешно изменен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))})
    @PatchMapping("/setProfile")
    public ResponseEntity<Object> setUserInf(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class), examples = {
                            @ExampleObject(name = "example_request", value = "{\"firstname\": \"example_firstname\", \"surname\": \"example_surname\", \"lastname\": \"example_lastname\"}")
                    })
            )
            @RequestBody HashMap<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    String firstname = (String) request.get("firstname");
                    String surname = (String) request.get("surname");
                    String lastname = (String) request.get("lastname");

                    UserModel newUser = userService.updateUserInfo(firstname, surname, lastname, user.get());

                    return ResponseEntity.ok(newUser);
                } else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Добавить фото для задачки подзадачи")
    @PostMapping("/addImage")
    public ResponseEntity<Object> addImage(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Файл изображения", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestParam("file") MultipartFile file) {
        ReasonModel reason = new ReasonModel();
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (tokenService.validateToken(jwtToken)) {
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    if (user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)) {
                        String image = imageService.saveImage(file);
                        UserModel currentUser = userService.updateImage(image, user.get());
                        if (currentUser != null) {
                            return ResponseEntity.status(HttpStatus.OK).body(currentUser);
                        }
                        reason.setReason("Что то не там с запросом");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                    } else {
                        reason.setReason("The user is not an administrator");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                    }
                } else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
}
