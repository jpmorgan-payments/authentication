import unittest
from unittest.mock import Mock
from sendFirstRequest import send_first_request

class TestSendFirstRequest(unittest.TestCase):

    def test_send_first_request(self):
        # Mocking requests.post method to avoid actual HTTP requests
        mock_response = Mock()
        mock_response.json.return_value = {"status": "success"}
        with unittest.mock.patch('requests.post', return_value=mock_response) as mock_post:
            payload = {"key": "value"}
            url = "https://example.com/api"
            accessToken = "your_access_token"
            response = send_first_request(payload, url, accessToken)
            # Assert that requests.post was called with the correct arguments
            mock_post.assert_called_once_with(url, json=payload, headers={
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": accessToken
            })
            # Assert that the response matches the expected value
            self.assertEqual(response, {"status": "success"})

if __name__ == '__main__':
    unittest.main()
