package samples

import (
	"testing"
)

func TestSendFirstRequestMtls(t *testing.T) {
	url := "https://apigatewaycat.jpmorgan.com/accessapi/balance"
	status := SendFirstRequestMtls(url)
	if status != "200 OK" {
		t.Errorf("Expected status 200 OK, got %s", status)
	}
}
