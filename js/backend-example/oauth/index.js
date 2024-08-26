/**
 * This code sets up an Express server that acts as a proxy for forwarding requests to an API server.
 * It includes functionality to modify request bodies, handle proxy responses, and handle requests with digital signatures and access tokens.
 * Within this code JWT is same as digital signature.
 */

// Import required modules
const jwt = require("jsonwebtoken"); // For JSON Web Tokens
const express = require("express"); // Express framework for creating the server
const {
  createProxyMiddleware,
  responseInterceptor,
} = require("http-proxy-middleware"); // For creating proxy middleware
const { gatherAccessToken } = require("./oAuthHandler"); // Custom module for gathering access token
require("dotenv").config(); // For loading environment variables

// Create an Express application
const app = express();

// Middleware to parse JSON and URL-encoded request bodies
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Load endpoint URL and digital signature key from environment variables
const endpointUrl = process.env.ENDPOINT_URL;
const digitalSignatureKey = process.env.DIGITAL;

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

// Function to create proxy middleware configuration with access token
async function createProxyConfiguration(target, accessToken) {
  const options = {
    target,
    changeOrigin: true,
    selfHandleResponse: true,
    onProxyReq: async (proxyReq, req) => {
      proxyReq.setHeader("Authorization", `bearer ${accessToken}`);
      if (
        req.body &&
        req.method === "POST" &&
        req.path.includes("/tsapi/v1/payments")
      ) {
        const bodyData = JSON.stringify(req.body);
        proxyReq.setHeader("Content-Type", "application/json");
        proxyReq.setHeader("Content-Length", Buffer.byteLength(bodyData));
        proxyReq.write(bodyData);
        proxyReq.end();
      }
    },
    onProxyRes: responseInterceptor(handleProxyResponse),
  };
  return createProxyMiddleware(options);
}

// Function to create proxy middleware configuration with digital signature and access token
async function createProxyConfigurationForDigital(
  target,
  digitalSignature,
  accessToken
) {
  const options = {
    target,
    changeOrigin: true,
    selfHandleResponse: true,
    pathRewrite: {
      "^/digitalSignature": "",
    },
    onProxyReq: async (proxyReq, req) => {
      proxyReq.setHeader("Authorization", `bearer ${accessToken}`);
      if (req.body) {
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
  // Fix for some languages sending payment amount as a string
  if (req.body?.payments?.paymentAmount) {
    req.body.payments.paymentAmount = parseInt(req.body.payments.paymentAmount);
  }

  // Retrieve access token
  const accessToken = await gatherAccessToken();

  // Generate digital signature
  const digitalSignatureTest = generateDigitalSignature(
    digitalSignatureKey,
    req.body
  );

  // Create proxy configuration with digital signature and access token
  const func = await createProxyConfigurationForDigital(
    endpointUrl,
    digitalSignatureTest,
    accessToken
  );
  func(req, res, next);
});

// Middleware to handle other requests
app.use("/*", async (req, res, next) => {
  // Retrieve access token
  const accessToken = await gatherAccessToken();
  if (accessToken) {
    // Create proxy configuration with access token
    const func = await createProxyConfiguration(endpointUrl, accessToken);
    func(req, res, next);
  }
});

// Start the server on port 4242
app.listen(4242, () =>
  console.log(`Node server listening at http://localhost:4242`)
);
