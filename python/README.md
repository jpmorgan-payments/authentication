# Digital Signature Python

A python implementation of generating a digital signature

## Requirements

- Python3 install
- [Configured .env file](../README.md)

## How to run

You can run this code by running the tests or by including it in your codebase.

1. Confirm `.env` configuration

Ensure the certificate keys are configured in `.env` in this directory. You can find information on generating certificates at in our [readme](../README.md).It should include the following keys:

```yaml
PRIVATE=...
PUBLIC=
```

2. Paste your json body into the body variable

3. Install dependencies and start the test

```
pip install -r requirements.txt
python digital_signature_test.py
```
