package samples

import (
	"testing"
)

func TestSendFirstRequestOAuth(t *testing.T) {
	url := "https://api-mock.payments.jpmorgan.com/account/v2/accounts/balances"
	status := SendFirstRequest(url)
	if status != "200 OK" {
		t.Errorf("Expected status 200 OK, got %s", status)
	}
}
