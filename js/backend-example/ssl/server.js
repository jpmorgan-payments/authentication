/**
 * This code is a Node.js application that acts as a proxy server, forwarding requests to an API server. 
 * It includes functionality to modify request bodies, handle proxy responses, and create digital signatures for certain requests.
 * Within this code JWT is same as digital signature. 
 * 
 */
// Import required modules
const jwt = require("jsonwebtoken"); // For JSON Web Tokens
const express = require("express"); // Express framework for creating the server
const https = require("https"); // For making HTTPS requests
const {
  createProxyMiddleware,
  responseInterceptor,
} = require("http-proxy-middleware"); // For creating proxy middleware
require("dotenv").config(); // For loading environment variables

// Create an Express application
const app = express();

// Middleware to parse JSON and URL-encoded request bodies
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Load SSL key and certificate from environment variables
const httpsOptions = {
  key: process.env.KEY,
  cert: process.env.CERT,
};

// Load endpoint URL and digital signature key from environment variables
const endpointUrl = process.env.ENDPOINT_URL;
const digitalSignatureKey = process.env.DIGITAL;

// Function to modify the request body before proxying
var restream = function (proxyReq, req, res, options) {
  if (req.body) {
    let bodyData = JSON.stringify(req.body);
    proxyReq.setHeader("Content-Type", "application/json");
    proxyReq.setHeader("Content-Length", Buffer.byteLength(bodyData));
    proxyReq.write(bodyData);
  }
};

// Function to add logging to proxy responses
const handleProxyResponse = async (responseBuffer, proxyRes, req) => {
  const exchange = `[${req.method}] [${proxyRes.statusCode}] ${req.path} -> ${proxyRes.req.protocol}//${proxyRes.req.host}${proxyRes.req.path}`;
  console.log(exchange);
  if (proxyRes.headers["content-type"] === "application/json") {
    const data = JSON.parse(responseBuffer.toString("utf8"));
    return JSON.stringify(data);
  }
  return responseBuffer;
};

// Function to create proxy middleware configuration
async function createProxyConfiguration(target) {
  const options = {
    target,
    changeOrigin: true,
    selfHandleResponse: true,
    agent: new https.Agent(httpsOptions),
    onProxyReq: restream,
    onProxyRes: responseInterceptor(handleProxyResponse),
    onError: (err) => {
      console.error(err);
    },
  };
  return createProxyMiddleware(options);
}

// Function to create proxy middleware configuration with digital signature
async function createProxyConfigurationForDigital(target, digitalSignature) {
  const options = {
    target,
    changeOrigin: true,
    selfHandleResponse: true,
    agent: new https.Agent(httpsOptions),
    pathRewrite: {
      "^/digitalSignature": "",
    },
    onProxyReq: async (proxyReq, req) => {
      if (req.body) {
        // When sending a digital signature request content needs to be text/xml to be accepted
        proxyReq.setHeader("Content-Type", "text/xml");
        proxyReq.setHeader(
          "Content-Length",
          Buffer.byteLength(digitalSignature)
        );
        proxyReq.write(digitalSignature);
      }
    },
    onProxyRes: responseInterceptor(handleProxyResponse),
    onError: (err) => {
      console.error(err);
    },
  };
  return createProxyMiddleware(options);
}

// Function to generate a digital signature using JWT
function generateDigitalSignature(digital_key, body) {
  return jwt.sign(body, digital_key, { algorithm: "RS256" });
}

// Middleware to handle requests with a digital signature
app.use("/digitalSignature/*", async (req, res, next) => {
  if (req.body?.payments?.paymentAmount) {
    req.body.payments.paymentAmount = parseInt(req.body.payments.paymentAmount);
  }
  const digitalSignatureTest = generateDigitalSignature(
    digitalSignatureKey,
    req.body
  );
  const func = await createProxyConfigurationForDigital(
    endpointUrl,
    digitalSignatureTest
  );
  func(req, res, next);
});

// Middleware to handle other requests
app.use("/*", async (req, res, next) => {
  const func = await createProxyConfiguration(endpointUrl);
  func(req, res, next);
});

// Start the server on port 4242
const server = app.listen(4242, () =>
  console.log(`Node server listening at http://localhost:4242`)
);

// Export the server for testing or other purposes
module.exports = server;
