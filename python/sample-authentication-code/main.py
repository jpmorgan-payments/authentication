from getAccessToken import get_access_token
from sendFirstRequest import send_first_request
from dotenv import load_dotenv
import os

load_dotenv()

'''
This snippet highlights how to access one of our OAuth api's using Python.
First we gather our access token using the code defined in the getAccessToken file.
Then we send the request using the code defined in sendFirstRequest file.
You will provide your CLIENT_ID, CLIENT_SECRET and ACCESS_TOKEN_URL in a .env file.
You will also include your API url for hitting your API. 
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
'''

access_token_url=os.getenv('ACCESS_TOKEN_URL')
client_id=os.getenv('CLIENT_ID')
client_secret=os.getenv('CLIENT_SECRET')
api_url = ""

def main():
    access_token = get_access_token(url=access_token_url, client_id=client_id, client_secret=client_secret)
    print('Gathered access token')
    response = send_first_request(url=api_url, access_token=access_token)
    print(response)

if __name__ == "__main__":
    main()