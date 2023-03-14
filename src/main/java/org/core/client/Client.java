package org.core.client;

import org.apache.http.HttpResponse;
import org.core.enums.AccessType;

import java.io.File;

public class Client {

    public static final String BASE_URL = "http://192.168.0.188:8082";

    public static HttpResponse doGet(String endpoint) {
        return Request.get(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.READ))
                .execute();
    }

    public static HttpResponse doPost(String endpoint, String body) {
        return Request.post(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doGet(String endpoint, String key, String value) {
        return Request.get(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.READ))
                .addParameter(key, value)
                .execute();
    }

    public static HttpResponse doPut(String endpoint, String body) {
        return Request.put(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doDelete(String endpoint, String body) {
        return Request.delete(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doPost(String endpoint, File file, String fileName) {
        return Request.post(BASE_URL + endpoint)
                .addBearerTokenAuth(AuthClient.getToken(AccessType.WRITE))
                .attachFileToBody(file, fileName)
                .execute();
    }
}
