package com.assignment.synchrony.controller;

import com.assignment.synchrony.model.Image;
import com.assignment.synchrony.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam MultipartFile file, @RequestParam Long userId) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File cannot be empty"));
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            return ResponseEntity.badRequest().body(Map.of("error", "File size exceeds 5MB"));
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
        }
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID"));
        }

        try {
            Map<String, String> response = imageService.uploadImage(file, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image - Imgur API is down", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable("imageId") Long imageId) {
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("Invalid image ID");
        }
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found with ID: " + imageId);
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Object> getImageDetails(@PathVariable("imageId") Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            return ResponseEntity.ok(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found with ID: " + imageId);
        }
    }
}

