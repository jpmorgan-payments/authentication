package samples

/*
* This utility script is used to create a certificate request (CSR) along with a self-signed X509 certificate (CRT) using
* crypto which is a go library containing various cryptography utilities to create and handle communicate protocols.
*
* generateCertificates.go is a script that will ask for various information that is incorporated into your certificate request
* which can then be used with an approved certificate authority (CA) to receive a signed certificate to on-board to PROD environment.
* The script will also generate a self-signed certificate using basic extensions that can be used to on-board to CAT environment.
*
* */

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"
	"fmt"
	"math/big"
	"os"
	"time"
)

func GenerateSelfSignedCert(countryName, stateOrProvinceName, localityName, organizationName, commonName, dnsName string) error {
	// Generate a private key
	privateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		return err
	}

	// Create a certificate template
	template := x509.Certificate{
		SerialNumber:          big.NewInt(1),
		Subject:               pkix.Name{Country: []string{countryName}, Province: []string{stateOrProvinceName}, Locality: []string{localityName}, Organization: []string{organizationName}, CommonName: commonName},
		NotBefore:             time.Now(),
		NotAfter:              time.Now().AddDate(1, 0, 0), // Expires after 365 days
		BasicConstraintsValid: true,
		IsCA:                  true,
		KeyUsage:              x509.KeyUsageCertSign | x509.KeyUsageKeyEncipherment | x509.KeyUsageDigitalSignature,
		ExtKeyUsage:           []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		DNSNames:              []string{dnsName},
	}

	// Create a self-signed certificate
	derBytes, err := x509.CreateCertificate(rand.Reader, &template, &template, &privateKey.PublicKey, privateKey)
	if err != nil {
		return err
	}

	// Write the certificate to a file
	certOut, err := os.Create("certificate.pem")
	if err != nil {
		return err
	}
	defer certOut.Close()
	pem.Encode(certOut, &pem.Block{Type: "CERTIFICATE", Bytes: derBytes})

	// Write the private key to a file
	keyOut, err := os.Create("./private_key.pem")
	if err != nil {
		return err
	}
	defer keyOut.Close()
	pem.Encode(keyOut, &pem.Block{Type: "RSA PRIVATE KEY", Bytes: x509.MarshalPKCS1PrivateKey(privateKey)})

	fmt.Println("Certificate and private key generated successfully.")
	return nil
}
