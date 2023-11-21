# Digital Signature GoLang snippet

A snippet of Golang code to show how to create a digital signature

## Requirements

- Go installed
- [Configured .env file](../README.md)

## How to run

You can run this code by running the tests or by including it in your codebase.

1. Confirm `.env` configuration

Ensure the certificate keys are configured in `.env` in this directory. You can find information on generating certificates at in our [readme](../README.md). It should include the following keys:

```yaml
PRIVATE=...
PUBLIC=
```

2. Install dependencies and start the server

```
go test
```
