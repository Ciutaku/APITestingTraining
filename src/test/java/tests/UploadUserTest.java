package tests;

import io.qameta.allure.Issue;
import org.core.client.UserClient;
import org.core.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

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
        int statusCode = client.uploadFile(file);

        List<User> usersAfterPost = client.getUsers().getBody();
        Assertions.assertAll("Asserting upload Users",
                () -> Assertions.assertEquals(201, statusCode, "Status Code is not 201"),
                () -> Assertions.assertTrue(usersAfterPost.containsAll(usersFromFile), "Response body does not contain all users from file"));
    }

    @Test
    @Issue("Uploading file with a user that has an unavailable zipcode gives incorrect status code and still adds the rest of the valid users")
    public void uploadUsersWithUnavailableZipcodeTest() {
        File file = new File("src/main/resources/" + scenarioTwoFile);
        List<User> usersFromFile = client.getUsersFromFile(file);
        int statusCode = client.uploadFile(file);

        List<User> usersAfterPost = client.getUsers().getBody();
        Assertions.assertAll("Asserting upload Users with one unavailable zipcode",
                () -> Assertions.assertEquals(424, statusCode, "Status Code is not 424"),
                () -> Assertions.assertFalse(usersAfterPost.containsAll(usersFromFile), "Response body contains users from file"));
    }

    @Test
    @Issue("Uploading file with a user that has missing required fields gives incorrect status code and deletes the entire user list")
    public void uploadUsersWithMissingRequiredFieldTest() {
        File file = new File("src/main/resources/" + scenarioThreeFile);
        List<User> usersFromFile = client.getUsersFromFile(file);
        int statusCode = client.uploadFile(file);

        List<User> usersAfterPost = client.getUsers().getBody();
        Assertions.assertAll("Asserting upload Users with one missing required field",
                () -> Assertions.assertEquals(409, statusCode, "Status Code is not 409"),
                () -> Assertions.assertFalse(usersAfterPost.containsAll(usersFromFile), "Response body contains users file"));
    }
}
