package tests.REST;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    private static final String LOGIN = "0oa157tvtugfFXEhU4x7";
    private static final String PASSWORD = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    @Test
    public void getWriteTokenTest() {
        given().auth().basic(LOGIN, PASSWORD)
                .params("grant_type", "client_credentials")
                .params("scope", "write")
                .get(BASE_URL + TOKEN_ENDPOINT)
                .then()
                .assertThat()
                .body("token", is(not(empty())));
    }

    @Test
    public void getReadTokenTest() {
        given().auth().basic(LOGIN, PASSWORD)
                .params("grant_type", "client_credentials")
                .params("scope", "read")
                .get(BASE_URL + TOKEN_ENDPOINT)
                .then()
                .assertThat()
                .body("token", is(not(empty())));
    }
}
