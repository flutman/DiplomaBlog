package com.example.diploma.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    String handleFileUpload(MultipartFile file);

    String handleFileUploadAvatar(MultipartFile file);

    byte[] getImage(Path path);
}
