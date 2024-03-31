package com.example.prodolymp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
@CrossOrigin(origins = "*")
public class PingController {

    @Operation(summary = "Просто пинг", description = "Проверка на работоспособность сервера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сервер работает"),
            @ApiResponse(responseCode = "500", description = "Сервер не работает")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "ok";
    }

}
