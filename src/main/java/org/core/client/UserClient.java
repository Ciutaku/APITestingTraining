package org.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.core.dto.UserToUpdate;
import org.core.enums.Gender;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UserClient {
    private static final String USERS_ENDPOINT = "/users";
    private static final String UPLOAD_USERS_ENDPOINT = "/users/upload";
    private final ObjectMapper objectMapper;

    public UserClient() {
        objectMapper = new ObjectMapper();
    }

    @Step
    public ResponseEntity<List<User>> getUsers() {
        ResponseEntity<List<User>> usersResponse = new ResponseEntity<>();
        HttpResponse response = Client.doGet(USERS_ENDPOINT);
        usersResponse.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            usersResponse.setBody(Arrays.stream(objectMapper
                    .readValue(response.getEntity().getContent(), User[].class)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usersResponse;
    }

    @Step
    public ResponseEntity<List<User>> getUsers(String key, String value) {
        ResponseEntity<List<User>> usersResponse = new ResponseEntity<>();
        HttpResponse response = Client.doGet(USERS_ENDPOINT, key, value);
        usersResponse.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            usersResponse.setBody(Arrays.stream(objectMapper
                    .readValue(response.getEntity().getContent(), User[].class)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usersResponse;
    }

    @Step
    public int postUser(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse response = Client.doPost(USERS_ENDPOINT, objectMapper.writeValueAsString(user));
            EntityUtils.consumeQuietly(response.getEntity());
            return response.getStatusLine().getStatusCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Step
    public int putUser(UserToUpdate user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse response = Client.doPut(USERS_ENDPOINT, objectMapper.writeValueAsString(user));
            EntityUtils.consumeQuietly(response.getEntity());
            return response.getStatusLine().getStatusCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Step
    public User createAvailableUser(String zipcode) {
        User user = new User(RandomUtils.nextInt(0, 120),
                RandomStringUtils.randomAlphabetic(10),
                Gender.FEMALE, zipcode);
        int statusCode = postUser(user);
        if (statusCode == 201) {
            return user;
        } else {
            throw new RuntimeException("Failed to create available user. Check POST /users method.");
        }
    }

    @Step
    public int deleteUser(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse response = Client.doDelete(USERS_ENDPOINT, objectMapper.writeValueAsString(user));
            EntityUtils.consumeQuietly(response.getEntity());
            return response.getStatusLine().getStatusCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Step
    public int uploadFile(File file) {
        HttpResponse response = Client.doPost(UPLOAD_USERS_ENDPOINT, file);
        EntityUtils.consumeQuietly(response.getEntity());
        return response.getStatusLine().getStatusCode();
    }

    @Step
    public List<User> getUsersFromFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.stream(mapper.readValue(file, User[].class)).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
