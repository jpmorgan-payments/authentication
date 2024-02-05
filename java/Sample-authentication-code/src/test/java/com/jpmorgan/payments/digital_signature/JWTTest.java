package com.jpmorgan.payments.digital_signature;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JWTTest

{

    Map<String, String> env = System.getenv();
    String privateKey = env.get("PRIVATE");
    String publicKey = env.get("PUBLIC");
    String payload = "{\"user_id\":123}";

    PublicKey formatPublicKey(String key) {
        String formattedKey = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replace("\\n", "").replaceAll("\\s+", "");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(formattedKey));
        PublicKey publicKey = null;
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(keySpecX509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return publicKey;
    }

    @Test
    void assertSignatureCreated() {
        String result = JWTDemo.createJWT(payload, privateKey);

        PublicKey pKey = formatPublicKey(publicKey);
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(pKey).build().parseClaimsJws(result);
        JSONObject obj = new JSONObject(jws.getBody());
        assertEquals(payload, obj.toString());
    }
}
