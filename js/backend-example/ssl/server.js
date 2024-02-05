const jwt = require("jsonwebtoken");
const express = require("express");
const https = require("https");
const {
  createProxyMiddleware,
  responseInterceptor,
} = require("http-proxy-middleware");
require("dotenv").config();

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const httpsOptions = {
  key: process.env.KEY,
  cert: process.env.CERT,
};

const endpointUrl = process.env.ENDPOINT_URL;
const digitalSignatureKey = process.env.DIGITAL;

// restream parsed body before proxying
var restream = function (proxyReq, req, res, options) {
  if (req.body) {
    let bodyData = JSON.stringify(req.body);
    // incase if content-type is application/x-www-form-urlencoded -> we need to change to application/json
    proxyReq.setHeader("Content-Type", "application/json");
    proxyReq.setHeader("Content-Length", Buffer.byteLength(bodyData));
    // stream the content
    proxyReq.write(bodyData);
  }
};

// Method to add logging to your proxy responses for debugging/info purposes
const handleProxyResponse = async (responseBuffer, proxyRes, req) => {
  const exchange = `[${req.method}] [${proxyRes.statusCode}] ${req.path} -> ${proxyRes.req.protocol}//${proxyRes.req.host}${proxyRes.req.path}`;
  console.log(exchange);
  if (proxyRes.headers["content-type"] === "application/json") {
    const data = JSON.parse(responseBuffer.toString("utf8"));
    return JSON.stringify(data);
  }
  return responseBuffer;
};

// Proxy function to forward any requests straight through to the API server
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

// Proxy function to forward any requests straight through to API server with added digital signature in the body
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

//Method for creating signed body for digital signature
function generateDigitalSignature(digital_key, body) {
  return jwt.sign(body, digital_key, { algorithm: "RS256" });
}

app.use("/digitalSignature/*", async (req, res, next) => {
  // Fix for some languages sending payment amount as a string
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

app.use("/*", async (req, res, next) => {
  const func = await createProxyConfiguration(endpointUrl);
  func(req, res, next);
});

const server = app.listen(4242, () =>
  console.log(`Node server listening at http://localhost:4242`)
);

module.exports = server;
