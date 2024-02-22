# Go Backend Samples

There are two code samples in this folder for connecting to our APIs using Go.
Each sample is categorised by the authentication method you will use.

** Note this is not production code and is supplied to get developers started **

## Requirements

- Go 1.19

## How to run - OAuth

1. Enter your access token url, client_id and client_secret in the file.

2. Start the server

```
go run .
```

3. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))

## How to run - SSL

1. Enter your certificates pathd in the file.To upload your certificates you will need to onboard to [https://developer.jpmorgan.com](https://developer.jpmorgan.com)
2. Start the server

```
go run .
```

3. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))
