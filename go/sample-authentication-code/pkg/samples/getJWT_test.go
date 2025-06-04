package samples

import (
	"fmt"
	"os"
	"testing"

	"github.com/golang-jwt/jwt/v5"
)

func TestDigSignGenerated(t *testing.T) {
	result := GenerateDigitalSignature(map[string]interface{}{"foo": "bar"})
	// Verify that the token can be parsed and verified
	token, err := jwt.Parse(result, func(token *jwt.Token) (interface{}, error) {
		// Verify that the signing method is RS256
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
		}
		// Load the public key from the .env file
		publicKey, err := os.ReadFile(os.Getenv("PUBLIC_KEY_PATH"))
		if err != nil {
			return nil, fmt.Errorf("Failed to read public key: %v", err)
		}
		key, err := jwt.ParseRSAPublicKeyFromPEM([]byte(publicKey))
		if err != nil {
			return nil, fmt.Errorf("Failed to parse public key: %v", err)
		}
		return key, nil
	})

	// Check if the token was parsed and verified successfully
	if err != nil {
		t.Errorf("Failed to verify token: %v", err)
	}
	if !token.Valid {
		t.Errorf("Invalid token")
	}
}
