import requests

'''
This snippet is used to send a request to our servers. 
Combine it with our getAccessToken to get started
'''

def send_first_request(payload, url, accessToken):
    headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
    "Authorization": "Bearer " + accessToken    
    }
    response = requests.post(url, json=payload, headers=headers)
    return response.json()

