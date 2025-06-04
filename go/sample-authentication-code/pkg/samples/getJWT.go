package samples

/*
This snippet is to show you how to generate a JWT/Digital Signature for sending post requests to our payments APIs.
You will need to provide your signing key and the body you wish to encode.
*/
import (
	"fmt"
	"os"

	"github.com/golang-jwt/jwt/v5"
)

func GenerateDigitalSignature(body map[string]interface{}) string {
	digital_key, err := os.ReadFile(os.Getenv("PRIVATE_KEY_PATH_DIGITAL"))
	if err != nil {
		fmt.Println("Error reading private key:", err)
		return ""
	}
	// Create a new JWT token with the RS256 signing method and the provided body
	token := jwt.NewWithClaims(jwt.SigningMethodRS256, jwt.MapClaims(body))
	signKey, err := jwt.ParseRSAPrivateKeyFromPEM([]byte(digital_key))
	if err != nil {
		fmt.Println(err)
	}
	tokenString, err := token.SignedString(signKey)
	if err != nil {
		fmt.Println(err)
	}
	return tokenString
}
