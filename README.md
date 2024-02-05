# J.P. Morgan Authentication examples

Within this repository you can find example code for accessing our Payments APIs.
We have split the code by programming language for ease of use.
Each folder contains:

- sample-authentication-code repository: This contains code scripts for generating digital signatures (also known as JWTs), gathering access tokens and generating certificates.


### Generating test certificates

You can follow the below commands to get setup with some testing certificates. To use these against J.P. Morgan APIs you will need to onboard them on [developer.jpmorgan.com](developer.jpmorgan.com)

```bash
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out publickey.crt
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out pkcs8.key
```

## Contribution

These are simple example snippets.
We welcome any additional language contributions and any performance improvements/suggestions.
