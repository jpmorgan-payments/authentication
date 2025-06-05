package samples

/*
 The below method sends a request to the API using OAuth 2.0.
 We have called the get_access_token method to retrieve the access token from the OAuth 2.0 server.
 The access token is then used to authenticate the request to the API.
 You will need to replace the URL, client_id, and client_secret with your own values.
 You can follow the instructions https://developer.payments.jpmorgan.com/docs/quick-start#retrieve-your-access-token
 You can use this code as a reference.
*/

import (
	"fmt"
	"net/http"
	"os"
)

func SendFirstRequest(url string) string {

	accessToken, err := GetAccessToken(os.Getenv("ACCESS_TOKEN_URL"), os.Getenv("CLIENT_ID"), os.Getenv("CLIENT_SECRET"))
	if err != nil {
		fmt.Println("Error getting access token:", err)
		return ""
	}
	client := http.Client{}
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Println("Error making GET request:", err)
		return ""
	}
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Accept", "application/json")
	req.Header.Add("Authorization", "Bearer "+accessToken)

	res, err := client.Do(req)
	if err != nil {
		fmt.Println("Error reading response body:", err)
		return ""
	}

	return res.Status
}
