# Postman Collection: J.P. Morgan APIs

This comprehensive collection provides examples of mTLS and OAuth requests tailored for J.P. Morgan APIs.

Follow these steps to seamlessly import this file into your Postman environment: [Importing Data into Postman](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/)

## Using the OAuth Collection

1. Import the collection into Postman.
2. Refer to this guide to obtain your CLIENT_ID and CLIENT_SECRET: [J.P. Morgan Quick Start](https://developer.payments.jpmorgan.com/quick-start)
3. Insert the acquired values into the authorization section of the 'getAccessToken' request within the OAuth folder.
4. Execute the 'getAccessToken' request.
5. Proceed to execute other requests as needed.

## Leveraging the mTLS Collection

1. Import the collection into Postman.
2. Follow the directions provided to generate and onboard certificates from: [J.P. Morgan Developer Portal](https://developer.jpmorgan.com)
3. Upload the certificates to Postman by referring to: [Adding Client Certificates in Postman](https://learning.postman.com/docs/sending-requests/certificates/#adding-client-certificates)
4. Execute the requests as required to interact with the APIs securely.

## Generating a JWT/Digital Signature

1. Choose the appropriate Authentication method and navigate to the respective nested folder.
2. Integrate your private key into the Postman variables.
3. Copy the request body and paste it into the 'sign payload' request.
4. Execute the 'sign payload' request.
5. Proceed to execute the 'initiate a payment' request to complete the process.
