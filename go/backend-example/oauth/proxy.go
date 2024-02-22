package main

/*
This code is a simple example of how to run a backend server for OAuth connections.
You can send your request to localhost:8001/<api_path>
This code will receive the request, gather the OAuth token and then forward it to our servers.
You will need to provide your ACCESS_TOKEN_URL, CLIENT_ID, CLIENT_SECRET as command line arguments
You can run with: 'go run . --url="enter url" --id="enter client id" --secret="secret"'
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
If you are hitting our APIs that require a digital signature or JWT then provide your key as a command line argument
Note this is not production code and is supplied to get developers started
*/

import (
	"bytes"
	"encoding/json"
	"flag"
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
		log.Fatal(err)
	}
	access_token_url := flag.String("url", "access_token_url", "an access token url")
	client_id := flag.String("id", "client_id", "client id")
	client_secret := flag.String("secret", "client_secret", "client_secret")
	// If digital key is defined we will encode the payload
	digital_key := flag.String("key", "digital_key", "digital_key")
	flag.Parse()

	handler := func(p *httputil.ReverseProxy) func(http.ResponseWriter, *http.Request) {
		return func(w http.ResponseWriter, r *http.Request) {
			log.Println(r.URL)
			r.Host = remote.Host
			// Access token generation. We are using the code from our sample authentication folder
			accessToken, err := samples.GetAccessToken(*access_token_url, *client_id, *client_secret)
			if err != nil {
				log.Fatalf("Error getting access token: %v", err)
			}
			w.Header().Set("Authorization", "Bearer "+accessToken)
			// JWT/Digital Signature generation. This is required for some of our POST requests.
			if len(*digital_key) != 0 {
				modifyRequestBody(w, r, *digital_key)
			}

			p.ServeHTTP(w, r)
		}
	}

	proxy := httputil.NewSingleHostReverseProxy(remote)
	http.HandleFunc("/", handler(proxy))
	err = http.ListenAndServe(":8001", nil)
	if err != nil {
		log.Fatal(err)
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
