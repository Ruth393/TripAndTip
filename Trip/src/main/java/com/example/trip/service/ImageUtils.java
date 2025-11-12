package com.example.trip.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtils {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    public static void uploadImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path fullPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fullPath, file.getBytes());
    }

    public static String getImage(String fileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IOException("Image file name is null or empty.");
        }

        Path fullPath = Paths.get(UPLOAD_DIRECTORY, fileName);

        if (Files.notExists(fullPath)) {
            throw new IOException("File not found at: " + fullPath.toString());
        }

        byte[] byteImage = Files.readAllBytes(fullPath);
        return Base64.getEncoder().encodeToString(byteImage);
    }

}