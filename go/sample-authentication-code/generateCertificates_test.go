package sample_authentication_code

import (
	"os"
	"testing"
)

func TestGenerateSelfSignedCert(t *testing.T) {
	// Call the function with test certificate details
	err := generateSelfSignedCert("US", "California", "San Francisco", "My Organization", "localhost", "localhost")
	if err != nil {
		t.Errorf("Error generating certificate: %v", err)
	}

	// Check if the certificate and private key files are generated
	_, certErr := os.Stat("certificate.pem")
	_, keyErr := os.Stat("private_key.pem")
	if os.IsNotExist(certErr) || os.IsNotExist(keyErr) {
		t.Errorf("Certificate or private key file is not generated")
	}

	// Clean up generated files
	os.Remove("certificate.pem")
	os.Remove("private_key.pem")
}
