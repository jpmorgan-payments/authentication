package com.jpmorgan.payments.sample;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jpmorgan.payments.sample.DigitalSignatureGenerator;

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

    private static final String API_URL = "https://api-mock.payments.jpmorgan.com";

    @Value("${ACCESS_TOKEN_URL}")
    private String ACCESS_TOKEN_URL;

    @Value("${CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Autowired
    private DigitalSignatureGenerator digitalSignatureGenerator;

    private final HttpClient httpClient;

    @Autowired
    public PaymentsController(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * A reverse proxy that forwards requests to the J.P. Morgan API and returns the
     * response to the client.
     * The reverse proxy should use the OAuth 2.0 client credentials flow to
     * authenticate with the J.P. Morgan API.
     * The reverse proxy should be able to handle multiple concurrent requests.
     * The reverse proxy should log the request and response payloads to the
     * console.
     * The reverse proxy should return a 502 Bad Gateway if the J.P. Morgan API is
     * unavailable.
     */

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
            case "POST" ->
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(handlePostBodyEncoding(body))).build();
            case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body)).build();
            case "DELETE" -> requestBuilder.DELETE().build();
            default -> throw new IllegalArgumentException("Unsupported request method: " + method);
        };
        HttpResponse<String> response = httpClient.send(newRequest,
                HttpResponse.BodyHandlers.ofString());
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    private String handlePostBodyEncoding(String body) {
        return digitalSignatureGenerator.createJWT(body);
    }

    /**
     * A method that uses the OAuth 2.0 client credentials flow to
     * authenticate with the J.P. Morgan API and returns the access token.§
     */

    private String getAccessToken() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("client_id", CLIENT_ID);
        parameters.put("client_secret", CLIENT_SECRET);
        parameters.put("scope", "jpm:payments:sandbox");
        String form = parameters.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ACCESS_TOKEN_URL))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();

        HttpResponse<?> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(response.body().toString());
        String accessToken = obj.getString("access_token");

        System.out.println(response.statusCode() + response.body().toString());
        return accessToken;
    }

}
