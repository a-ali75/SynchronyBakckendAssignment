# Synchrony Backend Service

A Spring Boot service for managing users and images, integrated with Imgur API for image uploads. This application allows user registration, image upload, retrieval, and deletion.

---

## **Getting Started**

### **Prerequisites**
Ensure the following are installed on your system:
- **Java** (JDK 17 or later)
- **Maven** (Version 3.6 or later)
- **Postman** (Optional, for API testing)

---

## **Setup and Running the Service**

### **Clone the Repository**
    git clone <repository_url>
    cd <repository_folder>

### **Update Configuration**
1. Navigate to `src/main/resources/application.properties` and configure the following:
   
    ### Server configuration
   server.port=8080

   ### Imgur API Configuration
    - imgur.client-id=your_client_id_here
    - imgur.client-secret=your_client_secret_here
    - imgur.refresh-token=your_refresh_token_here

   ### Database configuration (H2 for in-memory, MySQL for persistent storage)
   - spring.datasource.url=jdbc:h2:mem:testdb
   - spring.datasource.username=sa
   - spring.datasource.password=
   - spring.jpa.hibernate.ddl-auto=create-drop


2. Replace:
    - `your_client_id_here` with your Imgur Client ID.
    - `your_client_secret_here` with your Imgur Client Secret.
    - `your_refresh_token_here` with your Imgur Refresh Token.

### **Run the Application**
- Use Maven to build and run the service:
    ```
        mvn clean install
        mvn spring-boot:run
    ```

- The application will start on `http://localhost:8080`.

---

## **Running Tests**
1. **Run All Tests**:
    ```
    mvn test
    ```

2. **Run Specific Tests**:
   ```
   mvn -Dtest=UserServiceTest test
   mvn -Dtest=ImageControllerTest test
   ```

Test results will appear in the terminal. For detailed logs, refer to the test reports in the `target` directory.

---

## **Using Imgur API**

### **Client ID, Client Secret, and Refresh Token**
1. **Obtain Imgur Credentials**:
    - Go to [Imgur API Authorization](https://api.imgur.com/oauth2/addclient).
    - Create a new application and note down:
        - **Client ID**
        - **Client Secret**

2. **Generate a Refresh Token**:
    - Use Imgur's OAuth2 authorization flow:
      https://api.imgur.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&state=APPLICATION_STATE

    - Replace `YOUR_CLIENT_ID` with your actual Client ID and `YPUR_CLIENT_SECRET_CODE` with the client secret Id.
    - After authorization, Imgur will redirect to your callback URL with a `code` parameter.

3. **Update Tokens**:
    - Update the `imgur.client-id`, `imgur.client-secret`, and `imgur.refresh-token` properties in `application.properties`.

---

## **How to Get a New Refresh Token**
1. If your refresh token expires:
    - Use the same process above to reauthorize and regenerate a refresh token.
    - Replace the expired token in your configuration.

---

## **API Endpoints**

### **User APIs**
- **Register User**:
    ```
  POST /api/users/register
  Content-Type: application/json

  {
  "username": "john_doe",
  "password": "secure_password"
  }
  ```

- **Get User by ID**:
  ```GET /api/users/{userId}```

### **Image APIs**
- **Upload Image**:
  ```
  POST /api/images/upload
  Content-Type: multipart/form-data

  file: [image_file]
  userId: 1
  ```

- **Get Image by ID**:
  ```GET /api/images/{imageId}```

- **Delete Image**:
  ```DELETE /api/images/{imageId}```

---

## **Logs**
Application logs will appear in the console. You can customize log levels in `application.properties`:
```
logging.level.root=INFO
logging.level.com.assignment.synchrony=DEBUG
```

---

## **Contact**
For issues or contributions, feel free to open a GitHub issue or contact the author.
