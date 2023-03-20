package tests.REST;

import io.qameta.allure.Issue;
import org.core.client.AuthClient;
import org.core.client.UserClient;
import org.core.dto.User;
import org.core.enums.AccessType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UploadUserTest {
    private UserClient client;
    private final String scenarioOneFile = "scenario1.json";
    private final String scenarioTwoFile = "scenario2.json";
    private final String scenarioThreeFile = "scenario3.json";


    @BeforeEach
    public void init() {
        client = new UserClient();
    }

    @Test
    public void uploadUsersTest() {
        File file = new File("src/main/resources/" + scenarioOneFile);
        List<User> usersFromFile = client.getUsersFromFile(file);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("multipart/form-data")
                .multiPart(file)
                .post(BASE_URL + "/users/upload")
                .then().statusCode(201);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertTrue(Arrays.stream(users).toList().containsAll(usersFromFile));
    }

    @Test
    @Issue("Uploading file with a user that has an unavailable zipcode gives incorrect status code and still adds the rest of the valid users")
    public void uploadUsersWithUnavailableZipcodeTest() {
        File file = new File("src/main/resources/" + scenarioTwoFile);
        List<User> usersFromFile = client.getUsersFromFile(file);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("multipart/form-data")
                .multiPart(file)
                .post(BASE_URL + "/users/upload")
                .then().statusCode(424);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertFalse(Arrays.stream(users).toList().containsAll(usersFromFile));
    }

    @Test
    @Issue("Uploading file with a user that has missing required fields gives incorrect status code and deletes the entire user list")
    public void uploadUsersWithMissingRequiredFieldTest() {
        File file = new File("src/main/resources/" + scenarioThreeFile);
        List<User> usersFromFile = client.getUsersFromFile(file);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("multipart/form-data")
                .multiPart(file)
                .post(BASE_URL + "/users/upload")
                .then().statusCode(409);

        User[] users = given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .extract()
                .as(User[].class);

        assertFalse(Arrays.stream(users).toList().containsAll(usersFromFile));
    }
}
