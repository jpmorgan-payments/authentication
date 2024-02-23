package main

/*
This shows how to gather an access token and then send your first request to our OAuth protected APIs.
You will need to provide API_URL, ACCESS_TOKEN_URL, CLIENT_ID, CLIENT_SECRET and the payload.
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
* */

import (
	"fmt"
	"sample_authentication_code/pkg/samples"
	"strings"
)

func main() {
	api_url := ""
	access_token_url := ""
	client_id := ""
	client_secret := ""
	payload := strings.NewReader("{}")
	accessToken, err := samples.GetAccessToken(access_token_url, client_id, client_secret)
	if err != nil {
		fmt.Printf("Error getting access token: %v", err)
	} else {
		samples.SendFirstRequest(payload, api_url, accessToken)
	}
}
