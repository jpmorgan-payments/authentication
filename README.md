# J.P. Morgan Authentication examples

Within this repository you can find example code for accessing our Payments APIs.
We have split the code by programming language for ease of use.
Each folder contains:

- sample-authentication-code: This contains code scripts for generating digital signatures (also known as JWTs), gathering access tokens and generating certificates.
- backend-example: This is a fully functioning server that can handle sending requests to our APIs. (Coming soon to all languages!)

**Note this is not production code and is supplied to get developers started **

### Generating test certificates

You can follow the below commands to get setup with some testing certificates to get the code running. To use mTLS authentication with J.P. Morgan APIs follow the steps on our developer portal at: [https://developer.payments.jpmorgan.com/api/authentication](https://developer.payments.jpmorgan.com/api/authentication)

```bash
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out publickey.crt
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out pkcs8.key
```

## Contribution

These are simple example snippets.
We welcome any additional language contributions and any performance improvements/suggestions.
