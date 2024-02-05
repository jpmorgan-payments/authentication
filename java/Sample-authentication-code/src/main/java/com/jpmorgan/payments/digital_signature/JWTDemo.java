package com.jpmorgan.payments.digital_signature;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

public class JWTDemo

{

    private static String formatKeyString(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "").replaceAll("\\s+", "");
    }

    /**
     * Method for reading in a private key and preparing it for signing
     * 
     * @param key Your digital signature key that has been configured in
     *            developer.jpmorgan.com
     * @return a Private Key object
     */
    private static PrivateKey gatherPrivateKey(String key) {
        byte[] prvKeyBytes = DatatypeConverter.parseBase64Binary(formatKeyString(key));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(prvKeyBytes);
        PrivateKey prvKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            prvKey = kf.generatePrivate(keySpec);

        } catch (InvalidKeySpecException e) {
            // TODO Proper error handling is required here
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Proper error handling is required here
            e.printStackTrace();
        }
        return prvKey;
    }

    /**
     * Method for signing a JSON payload
     * 
     * @param payload The JSON payload to be digitally signed.
     * @param key     Your digital signature key that has been configured in
     *                developer.jpmorgan.com
     * @return
     */
    public static String createJWT(String payload, String key) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        // Prepare your private key for signing
        PrivateKey prvKey = gatherPrivateKey(key);
        final JSONObject obj = new JSONObject(payload);

        return Jwts.builder().setClaims(obj.toMap())
                .signWith(prvKey, signatureAlgorithm).compact();

    }
}
