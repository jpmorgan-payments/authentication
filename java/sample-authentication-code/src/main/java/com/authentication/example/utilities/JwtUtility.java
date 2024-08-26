package com.authentication.example.utilities;

import com.authentication.example.Constants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSSignerOption;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.opts.AllowWeakRSAKey;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
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
            Set<JWSSignerOption> options = Collections.singleton(AllowWeakRSAKey.getInstance());
            JWSSigner rsaSigner = new RSASSASigner(privateKey, options);
            signedJWT.sign(rsaSigner);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            LOG.info("Unable to create a valid JWT Client Assertion.", e);
            return null;
        }
    }

    private static String getThumbprint(Certificate certificate) {
        try {
            return DigestUtils.sha1Hex(certificate.getEncoded()).toUpperCase();
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("Error getting certificate thumbprint", e);
        }
    }
}
