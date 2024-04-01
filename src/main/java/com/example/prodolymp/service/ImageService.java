package com.example.prodolymp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    public String saveImage(MultipartFile file){
        try{
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());

            String uploadDir = "src/main/resources/static/images/";
            File destFile = new File(uploadDir + File.separator + fileName);
            file.transferTo(destFile);

            String imageUrl = "https://api.prod.webtm.ru/" + fileName;

            return imageUrl;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
