import requests


def send_first_request(payload, url, accessToken):
    headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
    "Authorization": "Bearer " + accessToken    
    }
    response = requests.post(url, json=payload, headers=headers)
    return response.json()

