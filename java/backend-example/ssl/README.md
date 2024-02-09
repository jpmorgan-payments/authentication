#  Java Example SSL Server

A Java implementation of a potential backend server. This is example code and will need additional configuration to be production ready. Never commit your certificats/secrets/keys etc.

## Requirements

- Java and maven installed
- Digital Signature key
- Transport certificate and key

## How to run

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
4. Run your code and try hitting "http://localhost:4242/api/digitalSignature/tsapi/v1/payments"
