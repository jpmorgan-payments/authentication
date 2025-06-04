from sendFirstRequestOAuth import send_first_request

def test_send_first_request_oauth():
    """Test the send_first_request function to ensure it sends a request with OAuth 2.0 authentication."""
    url = "https://api-mock.payments.jpmorgan.com/account/v2/accounts/balances"
    response = send_first_request(url)
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}"
    assert response.text, "Response text should not be empty"