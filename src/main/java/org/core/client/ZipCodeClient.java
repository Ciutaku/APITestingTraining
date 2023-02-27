package org.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.core.dto.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public void postZipcodes(String... zipcodes) {
        HttpResponse response = Client.doPost(POST_ZIPCODES_ENDPOINT, Arrays.toString(zipcodes));

    }

    public List<String> getDuplicates(List<String> afterPostZipCodes) {
        List<String> duplicatesList;
        duplicatesList = afterPostZipCodes.stream()
                .filter(e -> Collections.frequency(afterPostZipCodes, e) > 1)
                .distinct()
                .collect(Collectors.toList());
        return duplicatesList;
    }

}
