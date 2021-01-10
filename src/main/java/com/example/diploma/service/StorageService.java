package com.example.diploma.service;

import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.exception.PostErrorDto;
import com.example.diploma.exception.UploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
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

    //TODO добавить проверку на расширение файла png jpg
    private String fileUpload (MultipartFile file) {
        int ind = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        String ext = file.getOriginalFilename().substring(ind);

        Path path = generatePath("upload/");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new BadRequestException();
            }
        }

        String fileName = generateName();
        String imagePath = path.toString() + Path.of(fileName + ext).toString();
        //transfer and save file on server
        try {
            file.transferTo(Path.of(imagePath));
        } catch (Exception exception) {
            throw new UploadException();
        }
        imagePath = imagePath.replace("\\", "/");
       // imagePath = "img/upload/5NB.jpg";
        return imagePath;
    }

    public byte[] getImage(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
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
        return String.format("/%s",new String(text));
    }

    //TODO make resize uploaded image
    private static BufferedImage resizeImage(BufferedImage image) {
        final int WIDTH = 100;
        final int HEIGHT = 35;
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int widthStep = image.getWidth() / WIDTH;
        int heightStep = image.getHeight() / HEIGHT;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int rgb = image.getRGB(x * widthStep, y * heightStep);
                newImage.setRGB(x, y, rgb);
            }
        }
        return newImage;
    }
}
