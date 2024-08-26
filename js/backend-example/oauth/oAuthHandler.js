const axios = require("axios");
const qs = require("qs");

require("dotenv").config();

const { ACCESS_TOKEN_URL, ACCESS_TOKEN_USERNAME, ACCESS_TOKEN_SECRET } =
  process.env;
const scope = "jpm:payments:sandbox";

const authString = Buffer.from(
  `${ACCESS_TOKEN_USERNAME}:${ACCESS_TOKEN_SECRET}`
).toString("base64");
const data = "grant_type=client_credentials&scope=" + encodeURIComponent(scope);

const gatherAccessToken = () =>
  axios
    .post(ACCESS_TOKEN_URL, data, {
      headers: {
        Authorization: `Basic ${authString}`,
        "Content-Type": "application/x-www-form-urlencoded",
      },
    })
    .then((response) => {
      // Handle successful response
      return response.data.access_token;
    })
    .catch((error) => {
      // Handle error
      console.error("Error fetching access token:", error.message);
    });

module.exports = { gatherAccessToken };
