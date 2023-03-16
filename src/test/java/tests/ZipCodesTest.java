package tests;

import io.qameta.allure.Issue;
import org.core.client.ZipCodeClient;
import org.core.dto.ResponseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class ZipCodesTest {
    ZipCodeClient client = new ZipCodeClient();

    @Test
    @Issue("Status code is 201")
    public void getAllZipCodesTest() {
        ResponseEntity<List<String>> zipCodesList = client.getZipcodes();
        Assertions.assertEquals(200, zipCodesList.getStatusCode());
        Assertions.assertFalse(zipCodesList.getBody().isEmpty());
    }

    @Test
    public void postZipCodesTest() {
        client.postZipcodes("88888");
        ResponseEntity<List<String>> response = client.getZipcodes();
        Assertions.assertEquals(201, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("88888"));
    }

    @Test
    @Issue("Duplicated zipcodes from list are added")
    public void postDuplicateZipCodesInListTest() {
        client.postZipcodes("21345", "21345");
        ResponseEntity<List<String>> response = client.getZipcodes();
        Assertions.assertEquals(201, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("21345"));
        Assertions.assertEquals(1, Collections.frequency(response.getBody(), "21345"));
    }

    @Test
    @Issue("Duplicate zip code is successfully added")
    public void postDuplicateZipCodesInDBTest() {
        ResponseEntity<List<String>> getResponse = client.getZipcodes();
        String firstZipcode = getResponse.getBody().stream().toList().get(0);
        client.postZipcodes(firstZipcode);
        ResponseEntity<List<String>> response = client.getZipcodes();
        Assertions.assertEquals(201, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains(firstZipcode));
        Assertions.assertEquals(1, Collections.frequency(response.getBody(), firstZipcode));
    }
}


