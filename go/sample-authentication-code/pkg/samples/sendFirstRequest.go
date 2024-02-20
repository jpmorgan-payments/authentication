package samples

/*
This shows how to send your first request to our OAuth protected APIs.
It takes in a payload, API Url and then an access token.
* */

import (
	"fmt"
	"io"
	"net/http"
)

func SendFirstRequest(payload io.Reader, url string, accessToken string) {

	req, _ := http.NewRequest("POST", url, payload)

	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Accept", "application/json")
	req.Header.Add("Authorization", "Bearer "+accessToken)

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := io.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}
