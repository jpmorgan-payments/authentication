'''
This snippet is to show you how to generate a JWT/Digital Signature for sending post requests to our payments APIs.
You will need to provide your signing key and the body you wish to encode. 
'''
import jwt

def generate_digital_signature(digital_key, body):
    return jwt.encode(body, digital_key, algorithm="RS256")
