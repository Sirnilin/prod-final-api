package com.example.prodolymp.controllers;

import com.example.prodolymp.models.ReasonModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.service.TokenService;
import com.example.prodolymp.service.UserService;
import com.fasterxml.jackson.databind.JsonSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserControlller {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован успешно", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserModel.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с предоставленными учетными данными уже существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class), examples = {
                            @ExampleObject(name = "example_request", value = "{\"login\": \"example_login\", \"email\": \"example@example.com\", \"password\": \"example_password\"}")
                    })
            )
            @RequestBody Map<String, Object> request) {
        ReasonModel reason = new ReasonModel();
        if (request == null) {
            reason.setReason("Request body is missing");
            return ResponseEntity.status(HttpStatus.CREATED).body(reason);
        }
        System.out.println(request);
        String login = (String) request.get("login");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        int resultCode = userService.createUser(login, password, email);
        System.out.println(resultCode);
        if (resultCode == 0) {
            System.out.println("User registered successfully");
            UserModel user = userService.getUserByLogin(login);
            user.setPassword(null);
            Map<String, Object> response = new HashMap<>();
            response.put("profile", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (resultCode == 2) {
            reason.setReason("User with provided credentials already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(reason);
        } else {
            reason.setReason("Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
        }
    }

    @Operation(summary = "Авторизация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован успешно", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HashMap.class), examples = {
                    @ExampleObject(name = "example_response", value = "{\"token\": \"example_token_value\"}")
            })),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/sign-in")
    public ResponseEntity<Object> singInUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные пользователя для регистрации",
            required = true,
            content = @Content(schema = @Schema(implementation = Map.class), examples = {
                    @ExampleObject(name = "example_request", value = "{\"login\": \"example_login\", \"password\": \"example_password\"}")
            })
    )
            @RequestBody Map<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(request == null){
            reason.setReason("Request body is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
        }
        String login = (String) request.get("login");
        String password = (String) request.get("password");
        UserModel user = userService.getUserByLogin(login);
        if(user == null){
            reason.setReason("Invalid login or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
        }
        String storedPassword = userService.getStoredPassword(login);

        if(!passwordEncoder.matches(password, storedPassword)){
            reason.setReason("Invalid login or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
        }

        String token = tokenService.generateToken(user.getId().toString());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
