package com.authentication.example.utilities;

import com.authentication.example.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/*
 **************************************************************************
 * Utility to call IDAnywhere                                             *
 **************************************************************************
 */
public class IdAnywhereUtility {

    public static JsonNode getAccessToken(String jwt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));

        String idaRequest = "client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer"
        + "&client_assertion=" + jwt
        + "&grant_type=client_credentials"
        + "&client_id=" + Constants.CLIENT_ID
        + "&resource=" + Constants.RESOURCE;

        HttpEntity<String> request = new HttpEntity<>(idaRequest, httpHeaders);

        JsonNode response = restTemplate.postForObject(Constants.ACCESS_TOKEN_URI, request, JsonNode.class);

        if (response != null)
            return response;
        else
            throw new Exception("Empty response returned from IDAnywhere");
    }
}
