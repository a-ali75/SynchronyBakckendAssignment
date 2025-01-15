package com.assignment.synchrony.service;

import com.assignment.synchrony.model.Image;
import com.assignment.synchrony.model.User;
import com.assignment.synchrony.repository.ImageRepository;
import com.assignment.synchrony.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ImgurService imgurService;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, ImgurService imgurService) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.imgurService = imgurService;
    }

    public Map<String, String> uploadImage(@Valid MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        try {
            Map<String, String> imageData = imgurService.uploadImage(file);
            if (imageData == null) {
                System.out.println("getting null");
            }
            Image image = new Image();
            assert imageData != null;
            image.setUrl(imageData.get("url"));
            image.setImageId(Long.valueOf(imageData.get("id")));
            image.setName(file.getOriginalFilename());
            image.setUser(user);
            imageRepository.save(image);
            return imageData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        // Delete from Imgur
        String imageHash = extractImageHash(image.getUrl());
        try {
            imgurService.deleteImage(imageHash);
            // Delete from database
            imageRepository.delete(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    // Extract image hash from Imgur URL
    private String extractImageHash(String url) {
        return url.substring(url.lastIndexOf("/") + 1).split("\\.")[0];
    }

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
    }
}

