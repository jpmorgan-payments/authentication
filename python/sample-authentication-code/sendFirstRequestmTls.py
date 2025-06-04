import requests, os

# The below method sends a request to the API using mTls.
# You will need to provide the certificate and key paths for mTls authentication as environment variables.
# To upload the certificate and key, you can follow the instructions in the documentation.
# https://developer.payments.jpmorgan.com/api/mtls-with-digital-signature
# You can use this code as a reference.

def send_first_request(request_url):
    """Sends a POST request to the specified URL using mTLS authentication."""
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
    }
    certificate_path = os.environ.get('CERTIFICATE_PATH')
    certificate_key_path = os.environ.get('CERTIFICATE_KEY_PATH')
    return requests.post(request_url, headers=headers, cert=(certificate_path, certificate_key_path))

if __name__ == "__main__":
    url = "https://apigatewaycat.jpmorgan.com/accessapi/balance"
    response = send_first_request(url)
    print(response.text)