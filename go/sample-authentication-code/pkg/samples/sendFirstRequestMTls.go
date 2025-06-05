package samples

/*
The below method sends a request to the API using mTls.
 You will need to provide the certificate and key paths for mTls authentication as environment variables.
 To upload the certificate and key, you can follow the instructions in the documentation.
 https://developer.payments.jpmorgan.com/api/mtls-with-digital-signature
 You can use this code as a reference.
*/

import (
	"crypto/tls"
	"fmt"
	"net/http"
	"os"
)

func SendFirstRequestMtls(url string) string {

	cert, _ := tls.LoadX509KeyPair(os.Getenv("CERTIFICATE_PATH"), os.Getenv("CERTIFICATE_KEY_PATH"))

	client := &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{
				Certificates: []tls.Certificate{cert},
			},
		},
	}
	req, err := http.NewRequest("POST", url, nil)
	if err != nil {
		fmt.Println("Error making POST request:", err)
		return ""
	}
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Accept", "application/json")

	res, err := client.Do(req)
	if err != nil {
		fmt.Println("Error reading response body:", err)
		return ""
	}

	return res.Status
}
