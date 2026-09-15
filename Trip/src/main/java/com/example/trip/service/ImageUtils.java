package com.example.trip.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public class ImageUtils {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    // שינוי: מחזיר String (שם הקובץ שנשמר בפועל) במקום void
    public static String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String uniqueName = UUID.randomUUID() + extension;

        Path fullPath = Paths.get(UPLOAD_DIRECTORY, uniqueName);
        Files.write(fullPath, file.getBytes());

        return uniqueName;
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