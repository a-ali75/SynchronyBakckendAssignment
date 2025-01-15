package com.assignment.synchrony.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ImgurService {

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.client-secret}")
    private String clientSecret;

    @Value("${imgur.refresh-token}")
    private String refreshToken;

    @Value("${imgur.token.url}")
    private String getImgurTokenUrl;

    @Value("${imgur.api.base}")
    private String getImgurApiBaseUrl;

//    private static final String IMGUR_API_BASE = "https://api.imgur.com/3/";
//    private static final String IMGUR_TOKEN_URL = "https://api.imgur.com/oauth2/token";

    private final RestTemplate restTemplate;

    public ImgurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Upload an image to Imgur
     */
    public Map<String, String> uploadImage(MultipartFile file) throws Exception {
        String url = getImgurApiBaseUrl + "image";

        // Get Bearer Token
        String accessToken = getAccessToken();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + accessToken);

        // Create body with the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource()); // Use getResource() for MultipartFile

        // Build request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make POST request to Imgur
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Parse response
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseData = response.getBody();
            if (responseData != null) {
                Object dataObject = responseData.get("data");
                if (dataObject instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) dataObject;
                    Map<String, String> result = new HashMap<>();
                    result.put("id", (String) data.get("id"));
                    result.put("url", (String) data.get("link"));
                    return result;
                }
            }
            throw new Exception("Unexpected response structure from Imgur API");
        } else {
            throw new Exception("Failed to upload image to Imgur: " + response.getStatusCode());
        }
    }

    // View Image Details
    public Map<String, Object> viewImage(String imageHash) throws Exception {
        String url = getImgurApiBaseUrl + "image/" + imageHash;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new Exception("Failed to fetch image details from Imgur: " + response.getStatusCode());
        }
    }

    // Delete Image
    public void deleteImage(String imageHash) throws Exception {
        String url = getImgurApiBaseUrl + "image/" + imageHash;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Failed to delete image from Imgur: " + response.getStatusCode());
        }
    }

    /**
     * Retrieve a Bearer Token using OAuth2
     */
    private String getAccessToken() throws Exception {
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare body for token request
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", refreshToken);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");

        // Build request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Make POST request to Imgur token endpoint
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getImgurTokenUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Parse token response
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseData = response.getBody();
            if (responseData != null) {
                return (String) responseData.get("access_token");
            }
            throw new Exception("Failed to retrieve access token: Empty response");
        } else {
            throw new Exception("Failed to retrieve access token: " + response.getStatusCode());
        }
    }
}
