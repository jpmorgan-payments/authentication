package com.jpmorgan.payments.sample;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;

@Service
public class DigitalSignatureGenerator

{
    String privateKeyFilename = "./src/main/resources/static/private.key";
    private static String formatKeyString(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "").replaceAll("\\s+", "");
    }

    /**
     * Method for reading in a private key and preparing it for signing
     *
     * @return a Private Key object
     */
    private PrivateKey gatherPrivateKey() {
        byte[] keyBytes = readCertFile(privateKeyFilename);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey prvKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            prvKey = kf.generatePrivate(keySpec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            // TODO Proper error handling is required here
            e.printStackTrace();
        }
        return prvKey;
    }

    private byte[] readCertFile (String filename){
        try {
            String key = Files.readString(Paths.get(filename));
            return DatatypeConverter.parseBase64Binary(formatKeyString(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject formatPaymentAmountField (String payload){
        final JSONObject payloadObj = new JSONObject(payload);
        JSONObject payments = payloadObj.getJSONObject("payments");
        String amount  = payments.getString("paymentAmount");
        payments.put("paymentAmount", Double.parseDouble(amount));
        payloadObj.put("payments", payments);
        return payloadObj;
    }

    /**
     * Method for signing a JSON payload
     *
     * @param payload The JSON payload to be digitally signed.
     * @return
     */
    public String createJWT(String payload) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        // Prepare your private key for signing
        PrivateKey prvKey = gatherPrivateKey();
        JSONObject payments = formatPaymentAmountField(payload);

        return Jwts.builder().setClaims(payments.toMap())
                .signWith(prvKey, signatureAlgorithm).compact();

    }
}
