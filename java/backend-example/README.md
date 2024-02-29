# Java Backend Samples

In this folder there are two code samples for connecting to our APIs using node js.
Each sample is categorised by the authentication method you will use.

** Note this is not production code and is supplied to get developers started **

## Requirements

- Java and maven installed

## How to run - OAuth

1. You will require CLIENT_ID, CLIENT_SECRET and ACCESS_TOKEN_URL to run this code.
   You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start.
   Insert them into your application-local.yml file.
2. Ensure profile "local" is activated
3. Update line 23 in [Digital Signature Generator](./src/main/java/com/jpmorgan/payments/sample/DigitalSignatureGenerator.java) to match your digital signature key file.
4. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))

## How to run - SSL

1. You will need to setup a java keystore using your transport certificates. The below commands can be used to achieve this:

\*\* Make sure you remember the passwords you enter as you will need these later on \*\*

```bash
openssl pkcs12 -export -in transport.crt -inkey transport.key -out keystore.p12 -name "mykey"
keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -destkeystore truststore.jks -deststoretype JKS
```

2. We have provided an application.yml with some base setup. We recommend setting up an application-local.yml file with the path and password to your keystore. Do not commit this file.

```yml
ssl:
  keystore:
    location: <your_keystore_location>
    password: <your_keystore_password>
```

2. Ensure profile "local" is activated
3. Update line 23 in [Digital Signature Generator](./src/main/java/com/jpmorgan/payments/sample/DigitalSignatureGenerator.java) to match your digital signature key file
4. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))
