import requests

'''
This snippet is used to send a request to our servers. 
Combine it with our getAccessToken to get started
'''

def send_first_request(url, access_token):
    headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
    "Authorization": "Bearer " + access_token    
    }
    response = requests.get(url, headers=headers)
    return response.json()

