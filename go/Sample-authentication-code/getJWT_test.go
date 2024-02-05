package digital_signature

import (
    "fmt"
    "testing"
    "os"
	"github.com/joho/godotenv"
	"github.com/golang-jwt/jwt/v5"
	"strings"
)


func setupSuite(t testing.TB) string {
	godotenv.Load()
    return  os.Getenv("PRIVATE")
}

func TestDigSignGenerated(t *testing.T)  {
    privateKey := strings.ReplaceAll(setupSuite(t), "\\n", "\n")

    result := generate_digital_signature(privateKey, map[string]interface{}{"foo": "bar"})
	// Verify that the token can be parsed and verified
	token, err := jwt.Parse(result, func(token *jwt.Token) (interface{}, error) {
		// Verify that the signing method is RS256
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
		}
		// Load the public key from the .env file
		publicKey := strings.ReplaceAll(os.Getenv("PUBLIC"), "\\n", "\n")
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