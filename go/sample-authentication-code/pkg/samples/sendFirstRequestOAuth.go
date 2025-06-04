package samples

/*
This shows how to send your first request to our OAuth protected APIs.
It takes in a payload, API Url and then an access token.
* */

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
