package main

/*
This code is a simple example of how to run a backend server for SSL connections.
You can send your request to localhost:8001/<api_path>
This code will receive the request, add the certs and then forward it to our servers.
You will provide your PRIVATE and PUBLIC file paths.
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
If you are hitting our APIs that require a digital signature or JWT then provide your key as a command line value.E.g:
You can run with: 'go run . -key="your key here'
Note this is not production code and is supplied to get developers started
*/

import (
	"bytes"
	"crypto/tls"
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
	// Parse the remote URL
	remote, err := url.Parse("https://api-mock.payments.jpmorgan.com")
	if err != nil {
		log.Fatal(err)
	}
	// If digital key is defined we will encode the payload
	digital_key := flag.String("key", "digital_key", "digital_key")
	flag.Parse()

	// Load client certificate and key
	cert, err := tls.LoadX509KeyPair("client.crt", "client.key")
	if err != nil {
		log.Fatal(err)
	}

	// Configure TLS with client certificate (skip CA certificate verification)
	tlsConfig := &tls.Config{
		Certificates:       []tls.Certificate{cert},
		InsecureSkipVerify: true, // Skip CA certificate verification
	}

	// Create a HTTP client with custom TLS configuration
	client := &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: tlsConfig,
		},
	}

	// Define the reverse proxy handler
	handler := func(p *httputil.ReverseProxy) func(http.ResponseWriter, *http.Request) {
		return func(w http.ResponseWriter, r *http.Request) {
			log.Println(r.URL)
			r.Host = remote.Host

			// JWT/Digital Signature generation. This is required for some of our POST requests.
			modifyRequestBody(w, r, *digital_key)
			p.ServeHTTP(w, r)
		}
	}

	// Create a reverse proxy with the remote URL and custom HTTP client
	proxy := httputil.NewSingleHostReverseProxy(remote)
	proxy.Transport = client.Transport

	// Register the handler and start the server
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
