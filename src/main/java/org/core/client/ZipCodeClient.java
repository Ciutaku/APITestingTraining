package org.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.core.dto.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ZipCodeClient {

    private static final String GET_ZIPCODES_ENDPOINT = "/zip-codes";
    private static final String POST_ZIPCODES_ENDPOINT = "/zip-codes/expand";
    private final ObjectMapper objectMapper;

    public ZipCodeClient() {
        objectMapper = new ObjectMapper();
    }

    public ResponseEntity<List<String>> getZipcodes() {
        ResponseEntity<List<String>> zipcodesResponse = new ResponseEntity<>();
        HttpResponse response = Client.doGet(GET_ZIPCODES_ENDPOINT);
        zipcodesResponse.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            zipcodesResponse.setBody(Arrays.stream(objectMapper
                    .readValue(response.getEntity().getContent(), String[].class)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipcodesResponse;
    }

    public ResponseEntity<List<String>> postZipcodes(String... zipcodes) {
        ResponseEntity<List<String>> responseEntity = new ResponseEntity<>();
        HttpResponse response = Client.doPost(POST_ZIPCODES_ENDPOINT, Arrays.toString(zipcodes));
        responseEntity.setStatusCode(response.getStatusLine().getStatusCode());
        try {
            List<String> zipCodes = Arrays.stream(objectMapper.readValue(response.getEntity().getContent(), String[].class)).toList();
            responseEntity.setBody(zipCodes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseEntity;
    }

    public String createAvailableZipcode() {
        String zipcode = RandomStringUtils.randomNumeric(5);
        ResponseEntity<List<String>> responseEntity = postZipcodes(zipcode);
        if (responseEntity.getStatusCode() == 201) {
            return zipcode;
        } else {
            throw new RuntimeException("Failed to create available zipcode.");
        }
    }

}
