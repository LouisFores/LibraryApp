package com.example.librarybackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveBookCover(Long bookId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File ảnh không được để trống");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "cover" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);

        // uploads/books/{bookId}/
        Path folder = Paths.get(uploadDir, "books", String.valueOf(bookId));
        Files.createDirectories(folder);

        String filename = "cover-" + UUID.randomUUID() + ext;
        Path target = folder.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Vì static-locations=file:uploads/
        // file nằm ở: uploads/books/{id}/{filename}
        // nên URL sẽ là: /books/{id}/{filename}
        return "/books/" + bookId + "/" + filename;
    }
}
