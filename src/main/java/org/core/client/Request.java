package org.core.client;

import org.apache.http.HttpRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.core.enums.HttpMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class Request {

    private final HttpRequest request;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private Request(String url, HttpMethod method) {
        switch (method) {
            case GET -> request = new HttpGet(url);
            case POST -> request = new HttpPost(url);
            case PUT -> request = new HttpPut(url);
            case DELETE -> request = new HttpDelete(url);
            default -> throw new RuntimeException("Unknown request method");
        }
    }

    public static Request get(String url) {
        return new Request(url, HttpMethod.GET);
    }

    public static Request put(String url) {
        return new Request(url, HttpMethod.PUT);
    }

    public static Request post(String url) {
        return new Request(url, HttpMethod.POST);
    }

    public static Request delete(String url) {
        return new Request(url, HttpMethod.DELETE);
    }

    public Request addParameter(String key, String value) {
        try {
            URI uri = new URIBuilder(request.getRequestLine().getUri()).addParameter(key, value).build();
            ((HttpRequestBase) request).setURI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Request addHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    public Request addBasicAuth(String login, String password) {
        String authString = login + ":" + password;
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        addHeader("Authorization", "Basic " + encodedAuthString);
        return this;
    }

    public Request addBearerTokenAuth(String token) {
        addHeader("Authorization", "Bearer " + token);
        return this;
    }

    public Request addJsonBody(String body) {
        try {
            StringEntity entity = new StringEntity(body);
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public HttpResponse execute() {
        try {
            return httpClient.execute((HttpUriRequest) request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
