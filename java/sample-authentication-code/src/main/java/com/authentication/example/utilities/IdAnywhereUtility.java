package com.authentication.example.utilities;

import com.authentication.example.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/*
 **************************************************************************
 * Utility to call IDAnywhere                                             *
 **************************************************************************
 */
public class IdAnywhereUtility {

    private static final Logger LOG = LoggerFactory.getLogger("IdAnywhereUtility");

    public static JsonNode getAccessToken(String jwt) throws Exception {
        String idaRequest = "client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer"
                + "&client_assertion=" + jwt
                + "&grant_type=client_credentials"
                + "&client_id=" + Constants.CLIENT_ID
                + "&resource=" + Constants.RESOURCE;

        try {
            // Setup connection with IDAnywhere
            URL idaUrl = new URL(Constants.ACCESS_TOKEN_URI);
            HttpsURLConnection connection = (HttpsURLConnection) idaUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.connect();

            // Write IDAnywhere request to the output stream
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(idaRequest);
            wr.close();

            // Read in response from IDAnywhere
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Close the connection
            connection.disconnect();

            JsonNode jsonResponse = new ObjectMapper().readTree(response.toString());

            if (jsonResponse != null)
                return jsonResponse;
            else
                throw new Exception("Empty response returned from IDAnywhere");
        } catch(Exception e) {
            LOG.error("Error while sending request to IDAnywhere", e);
            throw e;
        }
    }
}
