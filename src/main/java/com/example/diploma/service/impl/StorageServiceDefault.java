package com.example.diploma.service.impl;

import com.example.diploma.exception.BadRequestException;
import com.example.diploma.exception.UploadException;
import com.example.diploma.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;

@Service
public class StorageServiceDefault implements StorageService {

    @Override
    public String handleFileUpload(MultipartFile file) {
        String response = "";
        if (!file.isEmpty()) {
            response = fileUpload(file);
        }
        return response;
    }

    @Override
    public String handleFileUploadAvatar(MultipartFile file) {
        String response = "";
        if (!file.isEmpty()) {
            response = fileUploadWithSize(file, 35, 35);
        }
        return response;
    }

    private String fileUploadWithSize(MultipartFile file, int imgH, int imgW) {
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
            String type = file.getContentType().split("/")[1];
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            img = resizeImage(img, imgH, imgW);
            ImageIO.write(img, type, new File(imagePath));
        } catch (Exception exception) {
            throw new UploadException();
        }
        imagePath = imagePath.replace("\\", "/");
        return imagePath;
    }


    private String fileUpload(MultipartFile file) {
        int ind = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        String ext = file.getOriginalFilename().substring(ind);
        Path path = generatePath("upload/");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new UploadException();
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
        return "/" + imagePath;
    }

    @Override
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

    private String generateName() {
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
        return String.format("/%s", new String(text));
    }

    private static BufferedImage resizeImage(BufferedImage image, int height, int weight) {
        BufferedImage newImage = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
        int widthStep = image.getWidth() / weight;
        int heightStep = image.getHeight() / height;
        for (int x = 0; x < weight; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x * widthStep, y * heightStep);
                newImage.setRGB(x, y, rgb);
            }
        }
        return newImage;
    }

}
