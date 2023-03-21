package tests.REST;

import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.core.client.AuthClient;
import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.User;
import org.core.dto.UserToUpdate;
import org.core.enums.AccessType;
import org.core.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateUserTest {
    private UserClient client;
    private ZipCodeClient zipCodeClient;
    private String zipcode;
    private User userToChange;

    @BeforeEach
    public void init() {
        client = new UserClient();
        zipCodeClient = new ZipCodeClient();
        zipcode = zipCodeClient.createAvailableZipcode();
        userToChange = client.createAvailableUser(zipcode);
    }

    @Test
    public void putUserTest() {
        User user = User.builder()
                .age(30)
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .zipCode(zipcode)
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(new UserToUpdate(userToChange, user))
                .put(BASE_URL + "/users")
                .then().statusCode(200);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().contains(user));
        assertFalse(Arrays.stream(users).toList().contains(userToChange));
    }

    @Test
    @Issue("User to update is deleted when PUT method body uses unavailable zipcode")
    public void putUserWithUnavailableZipcodeTest() {
        User user = User.builder()
                .age(30)
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .zipCode("UnavailableZipcode")
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(new UserToUpdate(userToChange, user))
                .put(BASE_URL + "/users")
                .then().statusCode(424);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().contains(userToChange));
        assertFalse(Arrays.stream(users).toList().contains(user));
    }

    @Test
    @Issue("User to update is deleted when PUT method body does not use mandatory fields")
    public void putUserWithMissingRequiredFieldsTest() {
        User user = User.builder()
                .age(30)
                .zipCode(zipcode)
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(new UserToUpdate(userToChange, user))
                .put(BASE_URL + "/users")
                .then().statusCode(409);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().contains(userToChange));
        assertFalse(Arrays.stream(users).toList().contains(user));
    }
}