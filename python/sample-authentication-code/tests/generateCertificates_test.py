import sys
sys.path.append('../')

import unittest
import os
import subprocess
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Import the function to be tested
from generateCertificates import generate_self_signed_cert

class TestGenerateCertificates(unittest.TestCase):
    def setUp(self):
        # Set up any necessary environment variables
        os.environ['COUNTRY_NAME'] = 'UK'
        os.environ['STATE_OR_PROVINCE_NAME'] = 'Scotland'
        os.environ['LOCALITY_NAME'] = 'Glasgow'
        os.environ['ORGANIZATION_NAME'] = 'My Organization'
        os.environ['COMMON_NAME'] = 'localhost'
        os.environ['DNS_NAME'] = 'localhost'

    def test_generate_self_signed_cert(self):
        # Call the function to generate the certificate
        generate_self_signed_cert()

        # Check if the certificate and private key files are generated
        self.assertTrue(os.path.exists("private_key.pem"))
        self.assertTrue(os.path.exists("certificate.pem"))

        # Use OpenSSL command to check if the certificate is valid
        try:
            subprocess.check_output(["openssl", "x509", "-in", "certificate.pem", "-noout", "-checkend", "0"])
        except subprocess.CalledProcessError:
            self.fail("Certificate is not valid.")

if __name__ == '__main__':
    unittest.main()
