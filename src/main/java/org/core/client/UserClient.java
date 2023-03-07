package org.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.core.dto.UserToUpdate;
import org.core.enums.Gender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UserClient {
    private static final String POST_USERS_ENDPOINT = "/users";
    private final ObjectMapper objectMapper;

    public UserClient() {
        objectMapper = new ObjectMapper();
    }

    public ResponseEntity<List<User>> getUsers() {
        ResponseEntity<List<User>> usersResponse = new ResponseEntity<>();
        HttpResponse response = Client.doGet(POST_USERS_ENDPOINT);
        usersResponse.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            usersResponse.setBody(Arrays.stream(objectMapper
                    .readValue(response.getEntity().getContent(), User[].class)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usersResponse;
    }

    public ResponseEntity<List<User>> getUsers(String key, String value) {
        ResponseEntity<List<User>> usersResponse = new ResponseEntity<>();
        HttpResponse response = Client.doGet(POST_USERS_ENDPOINT, key, value);
        usersResponse.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            usersResponse.setBody(Arrays.stream(objectMapper
                    .readValue(response.getEntity().getContent(), User[].class)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usersResponse;
    }

    public int postUser(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse response = Client.doPost(POST_USERS_ENDPOINT, objectMapper.writeValueAsString(user));
            return response.getStatusLine().getStatusCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int putUser(UserToUpdate user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse response = Client.doPut(POST_USERS_ENDPOINT, objectMapper.writeValueAsString(user));
            return response.getStatusLine().getStatusCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

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
}

