package com.example.prodolymp.controllers;

import com.example.prodolymp.models.*;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.service.ImageService;
import com.example.prodolymp.service.ProductService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final TokenService tokenService;
    private final ImageService imageService;

    @Operation(summary = "Получить все товары")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товары получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/")
    public ResponseEntity<Object> getAllProduct(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    List<ProductModel> productModels = productService.getAllProduct(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(productModels);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Купить товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно куплен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @PostMapping("/buy/{id}")
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
                    ProductModel product = productService.buyProduct(id, user.get());
                    if(product != null){
                        return ResponseEntity.status(HttpStatus.OK).body(product);
                    }
                    reason.setReason("Товара не существует или пользователю не хвататает денег");
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

    @Operation(summary = "Создать новый товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар создан", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("/new")
    private ResponseEntity<Object> createNewProduct(
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
                                            "  \"title\": \"String\",\n" +
                                            "  \"category\": \"String\",\n" +
                                            "  \"description\": \"String\",\n" +
                                            "  \"price\": \"Integer\",\n" +
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
                            Integer price = (Integer) request.get("price");

                            ProductModel product = productService.addProduct(title, category, description, price);

                            if(product == null){
                                reason.setReason("Bad title, category, description, price");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(product);
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

    @Operation(summary = "Получить все купленные товары пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товары получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
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
                    List<ProductModel> productModels = productService.getAllUserProduct(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(productModels);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Добавить фото для товара")
    @PostMapping("addImage/{Id}")
    public ResponseEntity<Object> addImageTask(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Файл изображения", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID задачи", required = true, example = "123", schema = @Schema(type = "integer"))
            @PathVariable Long Id){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(user.get().getRole() != null && user.get().getRole().equals(Role.ROLE_ADMIN)){
                        String image = imageService.saveImage(file);
                        ProductModel product = productService.addImage(image, Id);
                        if(product != null){
                            return ResponseEntity.status(HttpStatus.OK).body(product);
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
