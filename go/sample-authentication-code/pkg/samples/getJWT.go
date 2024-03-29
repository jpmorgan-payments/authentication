package samples

/*
This snippet is to show you how to generate a JWT/Digital Signature for sending post requests to our payments APIs.
You will need to provide your signing key and the body you wish to encode.
*/
import (
	"fmt"

	"github.com/golang-jwt/jwt/v5"
)

func GenerateDigitalSignature(digital_key string, body map[string]interface{}) string {
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
