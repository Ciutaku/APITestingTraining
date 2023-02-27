package org.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.core.dto.Auth;
import org.core.enums.AccessType;

import java.io.IOException;
import java.util.Locale;

import static org.core.client.Client.BASE_URL;

public class AuthClient {

    private final static String WRITE_TOKEN;
    private final static String READ_TOKEN;
    private static final String LOGIN = "0oa157tvtugfFXEhU4x7";
    private static final String PASSWORD = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    static {
        WRITE_TOKEN = extractToken(AccessType.WRITE);
        READ_TOKEN = extractToken(AccessType.READ);
    }

    private static String extractToken(AccessType accessType) {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpResponse response = Request.post(BASE_URL + TOKEN_ENDPOINT)
                .addParameter("grant_type", "client_credentials")
                .addParameter("scope", accessType.name().toLowerCase(Locale.ROOT))
                .addBasicAuth(LOGIN, PASSWORD)
                .execute();
        try {
            return objectMapper.readValue(response.getEntity().getContent(), Auth.class).getAccessToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getToken(AccessType accessType) {
        return accessType == AccessType.WRITE ? WRITE_TOKEN : READ_TOKEN;
    }
}
