package com.jpmorgan.payments.sample;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;


import javax.net.ssl.*;
import java.security.KeyStore;

@RestController
@RequestMapping("/api/digitalSignature")
public class PaymentsController {

    @Value("${ssl.keystore.location}")
    private String keystorePath;

    @Value("${ssl.keystore.password}")
    private String keystorePassword;

    @Value("${api.globalPaymentsPath}")
    private String globalPaymentsPath;
    @Value("${api.cat.url}")
    private String url;
    @Autowired
    DigitalSignatureGenerator digitalSignatureGenerator;
    @PostMapping("/tsapi/v1/payments")
    public ResponseEntity<String> digitalSignature(@RequestBody String body) throws Exception {
        String digitalSignature = digitalSignatureGenerator.createJWT(body);
        String outgoingUrl = url + globalPaymentsPath;
        RestTemplate restTemplate =  generateRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        HttpEntity<String> entity = new HttpEntity<>(digitalSignature, headers);
        return restTemplate.exchange(outgoingUrl, HttpMethod.POST, entity, String.class);
    }
    private RestTemplate generateRestTemplate () throws Exception {
        KeyStore keyStore = keyStore(keystorePath);

        // Create a KeyManagerFactory using the keystore
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        // We are only doing 1-way SSL for the code samples
        TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllManager() };


        // Create an SSLContext using the KeyManagerFactory and TrustManagerFactory
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);

        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build();

        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Create an HttpClient with the SSLContext and a custom hostname verifier
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        // Create an HttpComponentsClientHttpRequestFactory using the HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        // Create a RestTemplate using the requestFactory
        return  new RestTemplate(requestFactory);


    }

    private KeyStore keyStore(String file) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, keystorePassword.toCharArray());
        }
        return keyStore;
    }
}


