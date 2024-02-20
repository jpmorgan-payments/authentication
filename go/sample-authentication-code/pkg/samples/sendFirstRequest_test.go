package samples

import (
	"io/ioutil"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestSendFirstRequest(t *testing.T) {
	// Define the test payload, URL, and access token
	testPayload := strings.NewReader(`{"key": "value"}`)
	testURL := "https://example.com/api"
	testAccessToken := "abc123"

	// Create a new instance of the test server with a handler function
	ts := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		// Check if the request method is POST
		if r.Method != http.MethodPost {
			t.Errorf("Expected POST request, got %s", r.Method)
			return
		}

		// Check if the request URL matches
		if r.URL.String() != testURL {
			t.Errorf("Expected request URL to be %s, got %s", testURL, r.URL.String())
			return
		}

		// Read request body
		body, err := ioutil.ReadAll(r.Body)
		if err != nil {
			t.Errorf("Error reading request body: %v", err)
			return
		}

		// Check if the request body matches the expected payload
		expectedPayload := `{"key": "value"}`
		if string(body) != expectedPayload {
			t.Errorf("Expected request body:\n%s\nGot:\n%s", expectedPayload, string(body))
			return
		}

		// Respond with a predefined response
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		// You can customize the response body as needed for your test
		responseBody := `{"status": "success"}`
		w.Write([]byte(responseBody))
	}))

	// Close the server when the test finishes
	defer ts.Close()

	// Call the function being tested
	SendFirstRequest(testPayload, testURL, testAccessToken)
}
