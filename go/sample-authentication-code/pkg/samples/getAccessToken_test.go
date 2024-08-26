package samples

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGetAccessToken(t *testing.T) {
	// Create a mock HTTP server to handle the request
	mockServer := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {

		// Respond with a mock access token
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"access_token": "mock_access_token"}`))
	}))
	defer mockServer.Close()

	// Call the function with the URL of the mock server
	accessToken, err := GetAccessToken(mockServer.URL+"/token", "client_id", "client_secret")
	if err != nil {
		t.Errorf("Error getting access token: %v", err)
	}
	// Verify that the access token is correct
	expectedAccessToken := "mock_access_token"
	if accessToken != expectedAccessToken {
		t.Errorf("Expected access token %s, got %s", expectedAccessToken, accessToken)
	}
}
