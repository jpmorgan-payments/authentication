''''
This utility script is used to create a certificate request (CSR) along with a self-signed X509 certificate (CRT) using
cryptography which is a python library containing various cryptography utilities to create and handle communicate protocols.

generateCertificates.js is a script that will ask for various information that is incorporated into your certificate request
which can then be used with an approved certificate authority (CA) to receive a signed certificate to on-board to PROD environment.
The script will also generate a self-signed certificate using basic extensions that can be used to on-board to CAT environment.

You must include your certificate details in the .env file

'''

from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.x509.oid import NameOID
import datetime

def generate_self_signed_cert(COUNTRY_NAME, STATE_OR_PROVINCE_NAME, LOCALITY_NAME, ORGANIZATION_NAME, COMMON_NAME, DNS_NAME):
    # Generate a private key
    private_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=2048,
        backend=default_backend()
    )

    # Create a self-signed certificate
    subject = issuer = x509.Name([
        x509.NameAttribute(NameOID.COUNTRY_NAME, COUNTRY_NAME),
        x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, STATE_OR_PROVINCE_NAME),
        x509.NameAttribute(NameOID.LOCALITY_NAME, LOCALITY_NAME),
        x509.NameAttribute(NameOID.ORGANIZATION_NAME,ORGANIZATION_NAME),
        x509.NameAttribute(NameOID.COMMON_NAME, COMMON_NAME),
    ])
    cert = x509.CertificateBuilder().subject_name(
        subject
    ).issuer_name(
        issuer
    ).public_key(
        private_key.public_key()
    ).serial_number(
        x509.random_serial_number()
    ).not_valid_before(
        datetime.datetime.utcnow()
    ).not_valid_after(
        # Expires after 365 days
        datetime.datetime.utcnow() + datetime.timedelta(days=365)
    ).add_extension(
        x509.SubjectAlternativeName([x509.DNSName(DNS_NAME)]),
        critical=False,
    ).sign(private_key, hashes.SHA256(), default_backend())

    # Write the private key and certificate to files
    with open("private_key.pem", "wb") as f:
        f.write(private_key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.TraditionalOpenSSL,
            encryption_algorithm=serialization.NoEncryption()
        ))

    with open("certificate.pem", "wb") as f:
        f.write(cert.public_bytes(serialization.Encoding.PEM))

    print("Certificate and private key generated successfully.")
