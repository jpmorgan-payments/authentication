import unittest
import jwt
from digital_signature import generate_digital_signature
from dotenv import load_dotenv
import os

load_dotenv()  # Load the environment variables from the .env file


class TestGenerateDigitalSignature(unittest.TestCase):

    def setUp(self):
        self.private_key = os.environ.get("PRIVATE").replace("\\n", "\n")
        self.public_key = os.environ.get("PUBLIC").replace("\\n", "\n")
        self.body = {"user_id": 123}

    def test_generate_digital_signature(self):
        # Generate the digital signature using the function being tested
        signature = generate_digital_signature(self.private_key, self.body)
        # Verify that the signature was generated successfully
        self.assertIsNotNone(signature)

        # Verify that the signature can be decoded using the same digital key
        decoded = jwt.decode(signature, self.public_key, algorithms=["RS256"])
        self.assertDictEqual(decoded, self.body)


if __name__ == '__main__':
    unittest.main()
