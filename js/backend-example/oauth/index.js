const jwt = require("jsonwebtoken");
const express = require("express");
const {
  createProxyMiddleware,
  responseInterceptor,
} = require("http-proxy-middleware");
const { gatherAccessToken } = require("./oAuthHandler");
require("dotenv").config();

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const endpointUrl = process.env.ENDPOINT_URL;
const digitalSignatureKey = process.env.DIGITAL;

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
        // stream the content
        proxyReq.write(bodyData);
        proxyReq.end();
      }
    },
    onProxyRes: responseInterceptor(handleProxyResponse),
  };
  return createProxyMiddleware(options);
}
// Proxy function to forward any requests straight through to API server with added digital signature in the body
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

//Method for creating signed body for digital signature
function generateDigitalSignature(digital_key, body) {
  return jwt.sign(body, digital_key, { algorithm: "RS256" });
}

app.use("/digitalSignature/*", async (req, res, next) => {
  // Fix for some languages sending payment amount as a string
  if (req.body?.payments?.paymentAmount) {
    req.body.payments.paymentAmount = parseInt(req.body.payments.paymentAmount);
  }

  const accessToken = await gatherAccessToken();

  const digitalSignatureTest = generateDigitalSignature(
    digitalSignatureKey,
    req.body
  );
  const func = await createProxyConfigurationForDigital(
    endpointUrl,
    digitalSignatureTest,
    accessToken
  );
  func(req, res, next);
});

app.use("/*", async (req, res, next) => {
  const accessToken = await gatherAccessToken();
  if (accessToken) {
    const func = await createProxyConfiguration(endpointUrl, accessToken);
    func(req, res, next);
  }
});

app.listen(4242, () =>
  console.log(`Node server listening at http://localhost:4242`)
);
