package com.authentication.example.utilities;

import com.authentication.example.Constants;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
 **************************************************************************
 * Utility to call IDAnywhere                                             *
 **************************************************************************
 */
public class IdAnywhereUtility {

    public static JSONObject getAccessToken(String jwt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        formParams.put("client_assertion", jwt);
        formParams.put("grant_type", "client_credentials");
        formParams.put("client_id", Constants.CLIENT_ID);
        formParams.put("resource", Constants.RESOURCE);

        HttpEntity<String> request = new HttpEntity<>(
                formParams.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&")), httpHeaders);
        JSONObject response = restTemplate.postForObject(Constants.ACCESS_TOKEN_URI, request, JSONObject.class);

        if (response != null)
            return response;
        else
            throw new Exception("Empty response returned from IDAnywhere");
    }
}
