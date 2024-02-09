import sys
sys.path.append('../')
import unittest
import requests
from requests.exceptions import RequestException
from unittest.mock import patch, MagicMock
from getAccessToken import get_access_token

class TestGetAccessToken(unittest.TestCase):
    @patch('requests.post')
    def test_get_access_token_success(self, mock_post):
        # Mocking response object
        response_mock = MagicMock()
        response_mock.json.return_value = {"access_token": "mock_access_token"}
        mock_post.return_value = response_mock

        # Call the function
        access_token = get_access_token("mock_url", "mock_client_id", "mock_client_secret")

        # Assertions
        mock_post.assert_called_once_with(
            "mock_url",
            data={"grant_type": "client_credentials", "scope": "jpm:payments:sandbox"},
            auth=("mock_client_id", "mock_client_secret")
        )
        self.assertEqual(access_token, "mock_access_token")

    @patch('requests.post')
    def test_get_access_token_failure(self, mock_post):
        # Mocking response object to raise an exception
        mock_post.side_effect = RequestException("Mocked request exception")

        # Call the function
        with self.assertRaises(RequestException):
            get_access_token("mock_url", "mock_client_id", "mock_client_secret")

if __name__ == '__main__':
    unittest.main()
