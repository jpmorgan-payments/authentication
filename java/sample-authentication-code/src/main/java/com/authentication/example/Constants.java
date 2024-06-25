package com.authentication.example;

/*
 ***************************************************************************
 * Constants needed for generating JWT and retrieving access token.        *
 ***************************************************************************
 */
public class Constants {

    public static final String ACCESS_TOKEN_URI = "https://idag2.jpmorganchase.com/adfs/oauth2/token";

    // Provide certificate and client details
    public static final String CLIENT_ID = "CC-000000-A000000-000000-PROD";

    public static final String RESOURCE = "JPMC:URI:RS-000000-00000-Resource-PROD";

    public static final String CERTIFICATE_FILE_PATH = "C:/certificate.crt";

    public static final String PRIVATE_KEY_FILE_PATH = "C:/privateKey.key";

    public static final long EXPIRES_IN = 300000L;

}
