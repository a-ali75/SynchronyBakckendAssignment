package com.assignment.synchrony.service;

import com.assignment.synchrony.model.Image;
import com.assignment.synchrony.model.User;
import com.assignment.synchrony.repository.ImageRepository;
import com.assignment.synchrony.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImgurService imgurService;

    @InjectMocks
    private ImageService imageService;

    public ImageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Image image = new Image();
        image.setImageId(100L);
        image.setUrl("https://i.imgur.com/example.jpg");

        when(imageRepository.save(any(Image.class))).thenReturn(image);

        var response = imageService.uploadImage(mock(org.springframework.web.multipart.MultipartFile.class), 1L);

        assertEquals("https://i.imgur.com/example.jpg", response.get("url"));
        assertEquals("100L", response.get("imageId"));
    }

    @Test
    void testDeleteImage_Success() throws Exception {
        Image image = new Image();
        image.setImageId(1L);
        image.setUrl("https://i.imgur.com/example.jpg");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        doNothing().when(imgurService).deleteImage("example");

        imageService.deleteImage(1L);

        verify(imageRepository, times(1)).delete(image);
        verify(imgurService, times(1)).deleteImage("example");
    }

    @Test
    void testDeleteImage_NotFound() {
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> imageService.deleteImage(1L));

        assertEquals("Image not found with ID: 1", exception.getMessage());
        verify(imageRepository, times(1)).findById(1L);
    }
}
