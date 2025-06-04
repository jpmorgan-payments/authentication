import requests, os
from getAccessToken import get_access_token

# The below method sends a request to the API using OAuth 2.0.
# We have called the get_access_token method to retrieve the access token from the OAuth 2.0 server.
# The access token is then used to authenticate the request to the API.
# You will need to replace the URL, client_id, and client_secret with your own values.
# You can follow the instructions https://developer.payments.jpmorgan.com/docs/quick-start#retrieve-your-access-token
# You can use this code as a reference.

def send_first_request(request_url):
    """Sends a GET request to the specified URL using OAuth 2.0 authentication."""
    client_id = os.environ.get('CLIENT_ID')
    client_secret = os.environ.get('CLIENT_SECRET')
    access_token_url = os.environ.get('ACCESS_TOKEN_URL')
    access_token = get_access_token(access_token_url, client_id, client_secret)
    headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
    "Authorization": "Bearer " + access_token    
    }
    return requests.get(request_url, headers=headers)
    
if __name__ == "__main__":
    url = "https://api-mock.payments.jpmorgan.com/account/v2/accounts/balances"
    response = send_first_request(url)
    print(response.text)
    