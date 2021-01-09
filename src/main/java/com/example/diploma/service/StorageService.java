package com.example.diploma.service;

import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.exception.PostErrorDto;
import com.example.diploma.exception.UploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
public class StorageService {

    public String handleFileUpload(MultipartFile file) {
        String response = "";
        if (!file.isEmpty()) {
            response = fileUpload(file);
        }
        return response;
    }

    private String fileUpload(MultipartFile file) {
        final String BASE_IMAGE_PATH = "upload/";
        //get extension of uploaded file
        int ind = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        String ext = file.getOriginalFilename().substring(ind);

        Path path = generatePath(BASE_IMAGE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new BadRequestException();
            }
        }

        String fileName = generateName();
        String imagePath = path.toString() + fileName + ext;

        //transfer and save file on server
        try {
            file.transferTo(Path.of(imagePath));
        } catch (Exception exception) {
            throw new UploadException();
        }

        return imagePath;
    }

    private Path generatePath(String basePathName) {
        StringBuilder strPath = new StringBuilder(basePathName);
        int folderNameLength = 3;
        for (int i = 0; i < 3; i++) {
            generateString(folderNameLength);
            strPath.append(generateString(folderNameLength)).append("/");
        }
        return Path.of(strPath.toString());
    }

    private String generateName(){
        int fileNameLength = 3;
        return generateString(fileNameLength);
    }

    private String generateString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
