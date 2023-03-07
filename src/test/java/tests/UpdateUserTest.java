package tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.core.dto.User;
import org.core.dto.UserToUpdate;
import org.core.enums.Gender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        int statusCode = client.putUser(new UserToUpdate(userToChange, user));

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Put User test",
                () -> Assertions.assertEquals(200, statusCode, "Status Code is not 200"),
                () -> Assertions.assertTrue(usersResponseAfterPut.getBody().contains(user), "Response body does not contain user"),
                () -> Assertions.assertFalse(usersResponseAfterPut.getBody().contains(userToChange), "Response body contains old user"));
    }

    @Test
    public void putUserWithUnavailableZipcodeTest() {
        User user = User.builder()
                .age(30)
                .name(RandomStringUtils.randomAlphabetic(10))
                .sex(Gender.FEMALE)
                .zipCode("UnavailableZipcode")
                .build();

        int statusCode = client.putUser(new UserToUpdate(userToChange, user));

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Put User with Unavailable Zipcode test",
                () -> Assertions.assertEquals(424, statusCode, "Status Code is not 424"),
                () -> Assertions.assertFalse(usersResponseAfterPut.getBody().contains(user), "Response body contains new user"),
                () -> Assertions.assertTrue(usersResponseAfterPut.getBody().contains(userToChange), "Response body does not contain old user"));
    }

    @Test
    public void putUserWithMissingRequiredFieldsTest() {
        User user = User.builder()
                .age(30)
                .zipCode(zipcode)
                .build();

        int statusCode = client.putUser(new UserToUpdate(userToChange, user));

        ResponseEntity<List<User>> usersResponseAfterPut = client.getUsers();
        Assertions.assertAll("Asserting Put User with missing required fields test",
                () -> Assertions.assertEquals(409, statusCode, "Status Code is not 409"),
                () -> Assertions.assertFalse(usersResponseAfterPut.getBody().contains(user), "Response body contains new user"),
                () -> Assertions.assertTrue(usersResponseAfterPut.getBody().contains(userToChange), "Response body does not contain old user"));
    }
}
