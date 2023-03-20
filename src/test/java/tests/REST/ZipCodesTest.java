package tests.REST;

import io.qameta.allure.Issue;
import org.core.client.AuthClient;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.core.enums.AccessType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.core.client.Client.BASE_URL;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipCodesTest {
    ZipCodeClient client = new ZipCodeClient();

    @Test
    @Issue("Status code is 201")
    public void getAllZipCodesTest() {
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.READ))
                .get(BASE_URL + "/zip-codes")
                .then()
                .statusCode(200)
                .assertThat()
                .body(is(not(empty())));
    }

    @Test
    public void postZipCodesTest() {
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(List.of("88888"))
                .post(BASE_URL + "/zip-codes/expand")
                .then()
                .statusCode(201)
                .assertThat()
                .body(Matchers.containsString("88888"));
    }

    @Test
    @Issue("Duplicated zipcodes from list are added")
    public void postDuplicateZipCodesInListTest() {
        List<String> zipCodesList = List.of(given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(List.of("21345", "21345"))
                .post(BASE_URL + "/zip-codes/expand")
                .then()
                .statusCode(201)
                .assertThat()
                .body(containsString("21345"))
                .extract().asString());

        //can't get the assert to verify inside the list
        assertEquals(1, Collections.frequency(List.of(zipCodesList), "21345"));
    }

    @Test
    @Issue("Duplicate zip code is successfully added")
    public void postDuplicateZipCodesInDBTest() {
        ResponseEntity<List<String>> getResponse = client.getZipcodes();
        String firstZipcode = getResponse.getBody().stream().toList().get(0);
        given().header("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE))
                .contentType("application/json")
                .body(List.of(firstZipcode))
                .post(BASE_URL + "/zip-codes/expand")
                .then()
                .statusCode(201)
                .assertThat()
                .body(Matchers.containsString(firstZipcode));

        //can't get the assert to verify inside the list
        assertEquals(1, Collections.frequency(List.of(getResponse.getBody()), firstZipcode));
    }
}


