package tests.REST;

import io.qameta.allure.Issue;
import org.core.client.AuthClient;
import org.core.client.UserClient;
import org.core.client.ZipCodeClient;
import org.core.dto.User;
import org.core.enums.AccessType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;

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
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(availableUser)
                .delete(BASE_URL + "/users")
                .then().statusCode(204);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .assertThat()
                .body(Matchers.not(Matchers.containsString(String.valueOf(availableUser))));

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/zip-codes")
                .then()
                .assertThat()
                .body(Matchers.containsString(String.valueOf(availableUser.getZipCode())));
    }

    @Test
    @Issue("User is not removed when DELETE method body uses only required fields")
    public void deleteUserWithOnlyRequiredFieldsTest() {
        User user = User.builder()
                .name(availableUser.getName())
                .sex(availableUser.getSex())
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .delete(BASE_URL + "/users")
                .then().statusCode(204);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .assertThat()
                .body(Matchers.not(Matchers.containsString(String.valueOf(user))));

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/zip-codes")
                .then()
                .assertThat()
                .body(Matchers.containsString(String.valueOf(availableUser.getZipCode())));
    }

    @Test
    public void deleteUserWithMissingRequiredFieldsTest() {
        User user = User.builder()
                .age(availableUser.getAge())
                .zipCode(availableUser.getZipCode())
                .build();

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(user)
                .delete(BASE_URL + "/users")
                .then().statusCode(409);

        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/users")
                .then()
                .assertThat()
                .body(Matchers.containsString((availableUser.getName())));
    }
}
