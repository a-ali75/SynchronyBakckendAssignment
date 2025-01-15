package com.assignment.synchrony.controller;

import com.assignment.synchrony.model.User;
import com.assignment.synchrony.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Mocking input and service behavior
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");

        when(userService.registerUser(any(User.class))).thenReturn(mockUser);

        // Perform the test
        ResponseEntity<String> response = userController.registerUser(mockUser);

        // Assertions
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody());

        // Verify interactions
        verify(userService, times(1)).registerUser(mockUser);
    }

    @Test
    void testRegisterUser_MissingFields() {
        // Mocking invalid input (e.g., missing username)
        User mockUser = new User();
        mockUser.setPassword("password");

        // Perform the test
        ResponseEntity<String> response = userController.registerUser(mockUser);

        // Assertions
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid user data", response.getBody());

        // Verify no interaction with the service
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        // Mocking service behavior
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");

        when(userService.getUserById(1L)).thenReturn(mockUser);

        // Perform the test
        ResponseEntity<User> response = userController.getUserDetails(1L);

        // Assertions
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUser, response.getBody());

        // Verify interactions
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Mocking service behavior to throw an exception
        when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found with ID: 999"));

        // Perform the test
        ResponseEntity<User> response = userController.getUserDetails(999L);

        // Assertions
        assertEquals(404, response.getStatusCode().value());

        // Verify interactions
        verify(userService, times(1)).getUserById(999L);
    }
}
