package tests;

import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeleteUserTest {
    private UserClient client;
    private ZipCodeClient zipCodeClient;
    private String zipcode;
    private User availableUser;

    @BeforeEach
    public void init() {
        client = new UserClient();
        zipCodeClient = new ZipCodeClient();
        zipcode = zipCodeClient.createAvailableZipcode();
        availableUser = client.createAvailableUser(zipcode);
    }

    @Test
    public void deleteUserTest() {

        int statusCode = client.deleteUser(availableUser);

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Delete User test",
                () -> Assertions.assertEquals(204, statusCode, "Status Code is not 204"),
                () -> Assertions.assertFalse(usersResponseAfterPut.getBody().contains(availableUser), "Response body contains deleted user"),
                () -> Assertions.assertTrue(zipCodeClient.getZipcodes().getBody().contains(availableUser.getZipCode()), "Zipcode is not listed"));
    }

    @Test
    public void deleteUserWithOnlyRequiredFieldsTest() {
        User user = User.builder()
                .name(availableUser.getName())
                .sex(availableUser.getSex())
                .build();

        int statusCode = client.deleteUser(user);

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Delete User with only mandatory fields test",
                () -> Assertions.assertEquals(204, statusCode, "Status Code is not 204"),
                () -> Assertions.assertFalse(usersResponseAfterPut.getBody().contains(availableUser), "Response body contains deleted user"),
                () -> Assertions.assertTrue(zipCodeClient.getZipcodes().getBody().contains(availableUser.getZipCode()), "Zipcode is not listed"));
    }

    @Test
    public void deleteUserWithMissingRequiredFieldsTest() {
        User user = User.builder()
                .age(availableUser.getAge())
                .zipCode(availableUser.getZipCode())
                .build();

        int statusCode = client.deleteUser(user);

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Delete User with missing required fields test",
                () -> Assertions.assertEquals(409, statusCode, "Status Code is not 409"),
                () -> Assertions.assertTrue(usersResponseAfterPut.getBody().contains(availableUser), "Response body does not contain old user"));
    }
}
