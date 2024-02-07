import requests
'''
This snippet highlights how to obtain an OAuth access token using Python.
You will provide your CLIENT_ID, CLIENT_SECRET and ACCESS_TOKEN_URL in a .env file.
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
'''
import os
from dotenv import load_dotenv
load_dotenv()

CLIENT_ID=os.getenv('CLIENT_ID')
CLIENT_SECRET=os.getenv('CLIENT_SECRET')
ACCESS_TOKEN_URL=os.getenv('ACCESS_TOKEN_URL')


def get_access_token(url, client_id, client_secret):
    response = requests.post(
        url,
        data={"grant_type": "client_credentials", "scope":"jpm:payments:sandbox"},
        auth=(client_id, client_secret),
    )
    return response.json()["access_token"]


access_token = get_access_token(ACCESS_TOKEN_URL,CLIENT_ID,CLIENT_SECRET)
print(access_token)