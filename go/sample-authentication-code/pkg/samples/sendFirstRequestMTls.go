package samples

/*
This shows how to send your first request to our OAuth protected APIs.
It takes in a payload, API Url and then an access token.
* */

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
