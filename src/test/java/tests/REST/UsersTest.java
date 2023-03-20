package tests.REST;

import io.qameta.allure.Issue;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.core.client.AuthClient;
import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.core.enums.AccessType;
import org.core.enums.Gender;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    private UserClient client;
    private ZipCodeClient zipCodeClient;
    private String zipcode;

    @BeforeEach
    public void init() {
        client = new UserClient();
        RestAssured.baseURI = BASE_URL;
        zipCodeClient = new ZipCodeClient();
        zipcode = zipCodeClient.createAvailableZipcode();
    }

    @Test
    public void postUserWithAllFieldsTest() {
        User user = User.builder()
                .age(22)
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .zipCode(zipcode)
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "/users")
                .then().statusCode(201);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().contains(user));
        assertFalse(zipCodeClient.getZipcodes().getBody().contains(user.getZipCode()));
    }

    @Test
    public void postUserOnlyWithRequiredFieldsTest() {
        User user = User.builder()
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "/users")
                .then().statusCode(201);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().contains(user));
    }

    @Test
    public void postUserWithUnavailableZipcodeTest() {
        User user = User.builder()
                .age(30)
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .zipCode("UnavailableZipcode")
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "/users")
                .then().statusCode(424);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertFalse(Arrays.stream(users).toList().contains(user));
    }

    @Test
    @Issue("Status code is incorrect")
    public void postDuplicateUserTest() {
        ResponseEntity<List<User>> usersResponse = client.getUsers();
        User existingUser = usersResponse.getBody().get(0);
        User user = User.builder()
                .name(existingUser.getName())
                .sex(existingUser.getSex())
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "/users")
                .then().statusCode(400);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertFalse(Arrays.stream(users).toList().contains(user));
        assertEquals(1, Collections.frequency(List.of(users), existingUser));
    }

    @Test
    public void getUsersTest() {
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then().statusCode(200)
                .assertThat().body(is(not(empty())));
    }

    @Test
    public void olderThanParameterTest() {
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .param("olderThan", 29).when().get(BASE_URL + "/users")
                .then().statusCode(200)
                .assertThat()
                .body("age", Matchers.everyItem(Matchers.greaterThan(29)));
    }

    @Test
    public void youngerThanParameterTest() {
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .param("youngerThan", 19).when().get(BASE_URL + "/users")
                .then().statusCode(200)
                .assertThat()
                .body("age", Matchers.everyItem(Matchers.lessThan(19)));
    }
}
