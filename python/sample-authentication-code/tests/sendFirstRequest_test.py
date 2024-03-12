import unittest
from unittest.mock import Mock
from sendFirstRequest import send_first_request

class TestSendFirstRequest(unittest.TestCase):

    def test_send_first_request(self):
        # Mocking requests.post method to avoid actual HTTP requests
        mock_response = Mock()
        mock_response.json.return_value = {"status": "success"}
        with unittest.mock.patch('requests.get', return_value=mock_response) as mock_get:
            url = "https://example.com/api"
            access_token = "your_access_token"
            response = send_first_request(url, access_token)
            # Assert that requests.post was called with the correct arguments
            mock_get.assert_called_once_with(url, headers={
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": "Bearer " + access_token  
            })
            # Assert that the response matches the expected value
            self.assertEqual(response, {"status": "success"})

if __name__ == '__main__':
    unittest.main()
