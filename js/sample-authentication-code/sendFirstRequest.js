/**
 * This script gives you the code for sending a request to one of our APIs
 */

const axios = require("axios");

const url = "https://api-mock.payments.jpmorgan.com/tsapi/v1/payments";

const sendFirstRequest = async (body, accessToken) => {
  try {
    const response = await axios.post(url, body, {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
    });
    console.log(response.data);
  } catch (error) {
    console.error(error);
  }
};
