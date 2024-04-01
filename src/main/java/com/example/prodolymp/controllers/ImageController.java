package com.example.prodolymp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class ImageController {

    @GetMapping("/images/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serverFile(@PathVariable String fileName) {
        String uploadDir = "src/main/resources/static/images/";

        Path imagePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource;

        try {
            resource = new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (!Files.isReadable(imagePath) || !Files.isRegularFile(imagePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String filename = imagePath.getFileName().toString(); // Получаем имя файла из пути

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);

    }
}
