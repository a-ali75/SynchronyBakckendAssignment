package com.assignment.synchrony.controller;

import com.assignment.synchrony.service.ImageService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    public ImageControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage_Success() throws Exception {
        var mockFile = mock(org.springframework.web.multipart.MultipartFile.class);
        when(imageService.uploadImage(mockFile, 1L))
                .thenReturn(Map.of("imageId", "100L", "url", "https://i.imgur.com/example.jpg"));

        ResponseEntity<Map<String, String>> response = imageController.uploadImage(mockFile, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("100L", Objects.requireNonNull(response.getBody()).get("imageId"));
        assertEquals("https://i.imgur.com/example.jpg", response.getBody().get("url"));
    }

    @Test
    void testGetImageDetails_NotFound() throws Exception {
        when(imageService.getImageById(1L)).thenThrow(new RuntimeException("Image not found with ID: 1"));

        ResponseEntity<Object> response = imageController.getImageDetails(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Image not found with ID: 1", response.getBody());
    }
}
