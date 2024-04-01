package com.example.prodolymp.controllers;

import com.example.prodolymp.models.CategoryModel;
import com.example.prodolymp.models.ReasonModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.service.CategoryService;
import com.example.prodolymp.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final TokenService tokenService;

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
                    List<CategoryModel> categoryList = categoryService.getAllCategory();
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
    @PostMapping("/addCategory")
    public ResponseEntity<Object> addNewCategory(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @RequestBody HashMap<String, Object> request){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    String value = (String) request.get("category");
                    CategoryModel category = categoryService.addCategory(value);
                    return ResponseEntity.status(HttpStatus.OK).body(category);
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
