package com.authentication.example;

import com.authentication.example.utilities.IdAnywhereUtility;
import com.authentication.example.utilities.JwtUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/*
 **************************************************************************
 * Main executable class to retrieve access token                         *
 **************************************************************************
 */
public class TokenGeneratorApplication {

	private static final Logger LOG = LoggerFactory.getLogger("TokenGeneratorApplication");

	public static void main(String[] args) {

		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		X509Certificate x509Certificate;
		PrivateKey rsaKey;

		// Create x509 certificate from provided certificate file
		LOG.info("Certificate file path: {}", Constants.CERTIFICATE_FILE_PATH);
		try(FileInputStream certificateFile = new FileInputStream(Constants.CERTIFICATE_FILE_PATH)) {
			CertificateFactory factory = CertificateFactory.getInstance("X509");
			x509Certificate = (X509Certificate) factory.generateCertificate(certificateFile);
			LOG.info("Successfully generated certificate from file");
		} catch (Exception e) {
			LOG.error("Error generating certificate from file", e);
			return;
		}

		// Create PrivateKey from provided key file
		LOG.info("Private key file path: {}", Constants.PRIVATE_KEY_FILE_PATH);
		try {
			Path privateKeyPath = Paths.get(Constants.PRIVATE_KEY_FILE_PATH);
			String key = new String(Files.readAllBytes(privateKeyPath));
			String privateKeyPEM = key
					.replace("\n", "")
					.replace("\r", "")
					.replaceAll("-+BEGIN([A-Za-z\\s]+)KEY-+", "")
					.replaceAll("-+END([A-Za-z\\s]+)KEY-+", "");
			byte[] decoded = Base64.decodeBase64(privateKeyPEM);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
			rsaKey = keyFactory.generatePrivate(keySpec);
			LOG.info("Successfully generated private key from file");
		} catch (Exception e) {
			LOG.error("Error generating private key from file", e);
			return;
		}

		JsonNode idAnywhereResponse;
		// Retrieve access token from IDAnywhere
		try {
			LOG.info("Attempting to retrieve access token from IDAnywhere");
			idAnywhereResponse = IdAnywhereUtility.getAccessToken(JwtUtility.getClientJwtAssertion(x509Certificate, rsaKey));
		} catch (Exception e) {
			LOG.error("Error retrieving access token", e);
			return;
		}

		try {
			LOG.info("Access Token: {}", idAnywhereResponse.get("access_token").asText());
			LOG.info("Expires In: {}", idAnywhereResponse.get("expires_in").asText());
			LOG.info("Token Type: {}", idAnywhereResponse.get("token_type").asText());
		} catch (Exception e) {
			LOG.error("Error while extracting IDA response: {}", idAnywhereResponse);
		}
	}

}
