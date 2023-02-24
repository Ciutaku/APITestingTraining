package org.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {

    public static List<String> getZipCodeValues(HttpResponse content) throws IOException {

        ArrayList<String> zipCodesList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode arrayNode = objectMapper.readTree(content.getEntity().getContent());
        for (Iterator<JsonNode> it = arrayNode.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            String value = node.textValue();
            zipCodesList.add(value);
        }
        return zipCodesList;
    }

    public static List<String> getDuplicates(List<String> afterPostZipCodes) {
        List<String> duplicatesList;
        duplicatesList = afterPostZipCodes.stream()
                .filter(e -> Collections.frequency(afterPostZipCodes, e) > 1)
                .distinct()
                .collect(Collectors.toList());
        return duplicatesList;
    }
}
