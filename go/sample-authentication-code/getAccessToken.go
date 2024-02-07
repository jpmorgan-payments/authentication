package sample_authentication_code

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

func getAccessToken(url, clientID, clientSecret string) (string, error) {
	// Prepare request body
	payload := strings.NewReader("grant_type=client_credentials&scope=jpm:payments:sandbox")

	// Create HTTP client
	httpClient := &http.Client{}

	// Create HTTP request
	req, err := http.NewRequest("POST", url, payload)
	if err != nil {
		return "", err
	}
	req.SetBasicAuth(clientID, clientSecret)
	req.Header.Add("Content-Type", "application/x-www-form-urlencoded")

	// Send request
	resp, err := httpClient.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	// Decode response
	var responseMap map[string]interface{}
	err = json.NewDecoder(resp.Body).Decode(&responseMap)
	if err != nil {
		return "", err
	}

	// Extract access token from response
	accessToken, ok := responseMap["access_token"].(string)
	if !ok {
		return "", fmt.Errorf("access token not found in response")
	}

	return accessToken, nil
}
