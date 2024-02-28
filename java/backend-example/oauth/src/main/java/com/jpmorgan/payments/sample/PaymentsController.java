package com.jpmorgan.payments.sample;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class PaymentsController {

    static final String API_URL = "https://api-mock.payments.jpmorgan.com";

    @Value("${ACCESS_TOKEN_URL}")
    private String ACCESS_TOKEN_URL;

    @Value("${CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${CLIENT_SECRET}")
    private String CLIENT_SECRET;

    // Write a reverse proxy that forwards requests to the J.P. Morgan API
    // and returns the response to the client.
    // The reverse proxy should use the OAuth 2.0 client credentials flow to
    // authenticate with the J.P. Morgan API.
    // The reverse proxy should be able to handle multiple concurrent requests.
    // The reverse proxy should log the request and response payloads to the
    // console.
    // The reverse proxy should return a 502 Bad Gateway if the J.P. Morgan API is
    // unavailable.
    @RequestMapping(value = "/**")
    public ResponseEntity<String> proxyRequest(@RequestBody(required = false) String body,
            @RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> params,
            HttpServletRequest request) throws IOException, InterruptedException {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String url = API_URL + path;
        String query = params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        if (!query.isEmpty()) {
            url += "?" + query;
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken());
        HttpRequest newRequest = switch (method) {
            case "GET" -> requestBuilder.GET().build();
            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body)).build();
            case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body)).build();
            case "DELETE" -> requestBuilder.DELETE().build();
            default -> throw new IllegalArgumentException("Unsupported request method: " + method);
        };
        HttpResponse<String> response = HttpClient.newHttpClient().send(newRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Request: " + newRequest);
        System.out.println("Response: " + response);
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    public String getAccessToken() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("client_id", CLIENT_ID);
        parameters.put("client_secret", CLIENT_SECRET);
        parameters.put("scope", "jpm:payments:sandbox");
        String form = parameters.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ACCESS_TOKEN_URL))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();

        HttpResponse<?> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JSONObject obj = new JSONObject(response.body().toString());
        String accessToken = obj.getString("access_token");

        System.out.println(response.statusCode() + response.body().toString());
        return accessToken;
    }

}
