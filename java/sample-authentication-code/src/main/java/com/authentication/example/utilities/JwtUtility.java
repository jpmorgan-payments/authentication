package com.authentication.example.utilities;

import com.authentication.example.Constants;
import com.google.common.hash.Hashing;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Date;
import java.util.UUID;

/*
 **************************************************************************
 * Utility to create JWT                                                  *
 **************************************************************************
 */
public class JwtUtility {

    private static final Logger LOG = LoggerFactory.getLogger("JwtUtility");

    public static String getClientJwtAssertion(Certificate certificate, PrivateKey privateKey) {
        try {
            Date now = new Date();
            SignedJWT signedJWT = new SignedJWT(
                    (new JWSHeader.Builder(JWSAlgorithm.RS256)).keyID(getThumbprint(certificate)).build(),
                    (new com.nimbusds.jwt.JWTClaimsSet.Builder())
                            .jwtID(UUID.randomUUID().toString())
                            .subject(Constants.CLIENT_ID)
                            .audience(Constants.ACCESS_TOKEN_URI)
                            .issuer(Constants.CLIENT_ID)
                            .issueTime(new Date())
                            .expirationTime(new Date(now.getTime() + Constants.EXPIRES_IN))
                            .build());

            JWSSigner rsaSigner = new RSASSASigner(privateKey);
            signedJWT.sign(rsaSigner);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            LOG.info("Unable to create a valid JWT Client Assertion.", e);
            return null;
        }
    }

    private static String getThumbprint(Certificate certificate) {
        try {
            return Hashing.sha1().hashBytes(certificate.getEncoded()).toString().toUpperCase();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
