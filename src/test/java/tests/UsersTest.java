package tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.core.enums.Gender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class UsersTest {
    private UserClient client;
    private ZipCodeClient zipCodeClient;
    private String zipcode;

    @BeforeEach
    public void init() {
        client = new UserClient();
        zipCodeClient = new ZipCodeClient();
        zipcode = zipCodeClient.createAvailableZipcode();
    }


    @Test
    public void postUserWithAllFieldsTest() {

        User user = new User();
        user.setAge(30);
        user.setName(RandomStringUtils.randomAlphabetic(10));
        user.setSex(Gender.FEMALE);
        user.setZipCode(zipcode);

        int statusCode = client.postUsers(user);
        ResponseEntity<List<User>> usersResponse = client.getUsers();
        ResponseEntity<List<String>> zipcodes = zipCodeClient.getZipcodes();
        Assertions.assertAll("Asserting all fields test",
                () -> Assertions.assertEquals(201, statusCode, "Status Code is not 201"),
                () -> Assertions.assertTrue(usersResponse.getBody().contains(user), "Response body does not contain user"),
                () -> Assertions.assertFalse(zipcodes.getBody().contains(zipcode), "Zipcode is not removed from list")
        );
    }

    @Test
    public void postUserOnlyWithRequiredFieldsTest() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(10));
        user.setSex(Gender.FEMALE);

        int statusCode = client.postUsers(user);

        ResponseEntity<List<User>> usersAfterPostResponse = client.getUsers();
        Assertions.assertAll("Asserting required fields test",
                () -> Assertions.assertEquals(201, statusCode, "Status Code is not 201"),
                () -> Assertions.assertTrue(usersAfterPostResponse.getBody().contains(user), "Response body does not contain user")
        );
    }

    @Test
    public void postUserWithUnavailableZipcodeTest() {

        User user = new User();
        user.setAge(30);
        user.setName(RandomStringUtils.randomAlphabetic(10));
        user.setSex(Gender.FEMALE);
        user.setZipCode("UnavailableZipcode");

        int statusCode = client.postUsers(user);
        ResponseEntity<List<User>> usersResponse = client.getUsers();
        Assertions.assertAll("Asserting unavailable zipcode test",
                () -> Assertions.assertEquals(424, statusCode, "Status Code is not 424"),
                () -> Assertions.assertFalse(usersResponse.getBody().contains(user), "Response body contains user")
        );
    }

    @Test
    public void postDuplicateUserTest() {
        ResponseEntity<List<User>> usersResponse = client.getUsers();
        User existingUser = usersResponse.getBody().get(0);
        User user = new User();
        user.setName(existingUser.getName());
        user.setSex(existingUser.getSex());

        int statusCode = client.postUsers(user);
        ResponseEntity<List<User>> usersAfterPostResponse = client.getUsers();

        Assertions.assertAll("Asserting duplicate user test",
                () -> Assertions.assertEquals(400, statusCode, "Status Code is not 400"),
                () -> Assertions.assertEquals(1,
                        Collections.frequency(usersAfterPostResponse.getBody(), user), "Response body contains duplicated user")
        );
    }
}
