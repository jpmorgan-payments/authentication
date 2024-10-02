# NODE JS Access Token Authentication Sample

## Requirements

- Node v18+
- Updated config.json

## Required Client Details

The following values need to be updated in config/config.json:
* client_id
* resource_id
* certPath
* privateKey

Optionally, the following can be updated to reflect desired environment:
* audience
* ida_url

## How to Generate Access Token

1. Using the command line, navigate to directory:
    > authentication/js/sample-authentication-code
2. Run the following command to install required packages:
    > npm install
3. Run the getAccessToken script:
    > node .\getAccessToken.js

    **NOTE**: The access_token returned in the console may contain line breaks. Line breaks and any other whitespace needs to be removed and access_token sent as a single continuous string.