package com.jpmorgan.payments.sample;
 import java.security.cert.CertificateException;
        import java.security.cert.X509Certificate;
        import javax.net.ssl.X509TrustManager;

public class TrustAllManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Do nothing, to trust all client certificates without validation
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Do nothing, to trust all server certificates without validation
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
