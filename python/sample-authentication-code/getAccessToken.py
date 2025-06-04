"""
This snippet highlights how to obtain an OAuth access token using Python.
You will provide your CLIENT_ID, CLIENT_SECRET and ACCESS_TOKEN_URL.
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start
"""
import requests


def get_access_token(url, client_id, client_secret):
    response = requests.post(
        url,
        data={"grant_type": "client_credentials", "scope":"jpm:payments:sandbox"},
        auth=(client_id, client_secret),
    )
    return response.json()["access_token"]
