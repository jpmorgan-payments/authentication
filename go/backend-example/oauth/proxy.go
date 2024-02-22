package main

/*
This code is a simple example of how to run a backend server for OAuth connections.
You can send your request to localhost:8001/<api_path>
This code will receive the request, gather the OAuth token and then forward it to our servers.
If you are hitting our APIs that require a digital signature or JWT then uncomment line 40 and provide your key.
*/

import (
	"bytes"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
	"sample_authentication_code/pkg/samples"
)

func main() {
	remote, err := url.Parse("https://api-mock.payments.jpmorgan.com")
	if err != nil {
		panic(err)
	}
	access_token_url := ""
	client_id := ""
	client_secret := ""
	//digital_key := ""
	handler := func(p *httputil.ReverseProxy) func(http.ResponseWriter, *http.Request) {
		return func(w http.ResponseWriter, r *http.Request) {
			log.Println(r.URL)
			r.Host = remote.Host
			// Access token generation. We are using the code from our sample authentication folder
			accessToken, err := samples.GetAccessToken(access_token_url, client_id, client_secret)
			if err != nil {
				log.Printf("Error getting access token: %v", err)
			}
			w.Header().Set("Authorization", "Bearer "+accessToken)
			// JWT/Digital Signature generation. This is required for some of our POST requests.
			//modifyRequestBody(w, r, digital_key)

			p.ServeHTTP(w, r)
		}
	}

	proxy := httputil.NewSingleHostReverseProxy(remote)
	http.HandleFunc("/", handler(proxy))
	err = http.ListenAndServe(":8001", nil)
	if err != nil {
		panic(err)
	}
}

func modifyRequestBody(w http.ResponseWriter, r *http.Request, digital_key string) {
	var body map[string]interface{}
	err := json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		log.Printf("Error decoding request body: %v", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	digitalSignature := []byte(samples.GenerateDigitalSignature(digital_key, body))
	r.Body = io.NopCloser(bytes.NewBuffer(digitalSignature))
	r.ContentLength = int64(len(digitalSignature))
}
