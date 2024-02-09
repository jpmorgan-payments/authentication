import sys
sys.path.append('../')

import unittest
import os
import subprocess
from dotenv import load_dotenv
import os

# Load environment variables from .env file
load_dotenv()

COUNTRY_NAME=os.getenv('COUNTRY_NAME')
STATE_OR_PROVINCE_NAME=os.getenv('STATE_OR_PROVINCE_NAME')
LOCALITY_NAME=os.getenv('LOCALITY_NAME')
ORGANIZATION_NAME=os.getenv('ORGANIZATION_NAME')
COMMON_NAME=os.getenv('COMMON_NAME')
DNS_NAME=os.getenv('DNS_NAME')

# Import the function to be tested
from generateCertificates import generate_self_signed_cert

class TestGenerateCertificates(unittest.TestCase):
    def test_generate_self_signed_cert(self):
        # Call the function to generate the certificate
        generate_self_signed_cert(COUNTRY_NAME, STATE_OR_PROVINCE_NAME, LOCALITY_NAME, ORGANIZATION_NAME, COMMON_NAME, DNS_NAME)

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
