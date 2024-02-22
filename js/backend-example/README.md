# NODE JS Backend Samples

In this folder there are two code samples for connecting to our APIs using node js.
Each sample is categorised by the authentication method you will use.

** Note this is not production code and is supplied to get developers started **

## Requirements

- Node v18+
- Configured .env file

## How to run - OAuth

1. Configure a '.env' file in the repository by adding your CLIENT_ID and CLIENT_SECRET, directions can be followed here: [https://developer.payments.jpmorgan.com/quick-start#add-an-api-to-your-project](https://developer.payments.jpmorgan.com/quick-start#add-an-api-to-your-project) (** this file should not be committed**)

2. Install dependencies and start the server

```
yarn install
yarn start
```

3. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))

## How to run - SSL

1. Configure a '.env' file in the repository by adding your SSL Certificate and Key to the file (** this file should not be committed**). You can copy the layout in .env.example. To upload your certificates you will need to onboard to [https://developer.jpmorgan.com](https://developer.jpmorgan.com)
2. Install dependencies and start the server

```
yarn install
yarn start
```

3. Import the postman collection at the top of this repo to test out the server connection, you can use folder 'backend-examples'. Instructions for importing postman collections can be found ([here](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/))
