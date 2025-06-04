from sendFirstRequestmTls import send_first_request

def test_send_first_request_mtls():
    """Test the send_first_request function to ensure it sends a request with mtls authentication."""
    url = "https://apigatewaycat.jpmorgan.com/accessapi/balance"
    response = send_first_request(url)
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}"
    assert response.text, "Response text should not be empty"