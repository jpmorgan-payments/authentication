/*
* This utility script is used to create a certificate request (CSR) along with a self-signed X509 certificate (CRT) using
* node-forge which is a npm library containing various cryptography utilities to create and handle communicate protocols.
*
* generateCertificates.js is a script that will ask for various information that is incorporated into your certificate request
* which can then be used with an approved certificate authority (CA) to receive a signed certificate to on-board to PROD environment.
* The script will also generate a self-signed certificate using basic extensions that can be used to on-board to CAT environment.
*
* Note: node must be installed on your machine to download and install any external dependencies as well as run the individual
* javascript files. Please reference the documentation on https://nodejs.org/en/docs/ for any help on setup/installation.
* */

var prompt = require('prompt-sync')();
var forge = require('node-forge');
var fs = require('fs');

try {
    console.log('\n***Note: For PROD environment, a certificate must be created by a J.P. Morgan approved Certificate Authority (CA). ' +
        'Utility will use the information provided to create a certificate request which can then be given to a CA ' +
        'and a self-signed certificate which could be leveraged to on-board to the CAT environment.***');

    // user input for certificate request
    console.log('\nYou are about to be asked to enter information that will be incorporated into your certificate request.');
    var countryName = requirePrompt('Country Name (2 letter code): ');
    var stateName = requirePrompt('State or Province Name (2 letter code): ');
    var cityName = requirePrompt('Locality Name (eg, city): ');
    var organizationName = requirePrompt('Organization Name (eg, company): ');
    var organizationUnitName = requirePrompt('Organizational Unit Name (eg, section): ');
    var commonName = requirePrompt('Common Name (eg, identifier): ');

    // openssl genrsa -out key 2048
    console.log('\nGenerating 2048-bit key-pair...');
    var keys = forge.pki.rsa.generateKeyPair(2048);
    console.log('Key-pair created successfully...');

    // openssl req -new -config ../openssl.cnf -key key -out csr
    // Note: Does not actually use .cnf, read in .key or output .csr; done in-memory
    // Note: Could skip creating CSR here if you're the one generating the keys
    console.log('Creating certification request...');
    var csr = forge.pki.createCertificationRequest();
    csr.publicKey = keys.publicKey;
    csr.setSubject([
        {
            name: 'countryName',
            value: countryName.valueOf()
        },
        {
            shortName: 'ST',
            value: stateName.valueOf()
        },
        {
            name: 'localityName',
            value: cityName.valueOf()
        },
        {
            name: 'organizationName',
            value: organizationName.valueOf()
        },
        {
            shortName: 'OU',
            value: organizationUnitName.valueOf()
        },
        {
            name: 'commonName',
            value: commonName.valueOf()
        }
    ]);

    // sign certification request
    csr.sign(keys.privateKey);
    console.log('Certification request created...');

    // PEM-format keys and csr
    var pem = {
        privateKey: forge.pki.privateKeyToPem(keys.privateKey),
        publicKey: forge.pki.publicKeyToPem(keys.publicKey),
        csr: forge.pki.certificationRequestToPem(csr)
    };

    // Log PEM-formatted keys and csr, Uncomment code below if needed.
    // console.log('\nKey-Pair:');
    // console.log(pem.privateKey);
    // console.log(pem.publicKey);
    //
    // console.log('\nCertification Request:');
    // console.log(pem.csr);

    // write files to config directory
    fs.writeFileSync(__dirname + '/config/privateKey.key', pem.privateKey);
    fs.writeFileSync(__dirname + '/config/publicKey.key', pem.publicKey);
    fs.writeFileSync(__dirname + '/config/certificateRequest.pem', pem.csr);

    /*
    * Code below is to create sample self-signed certificate using CSR.
    * */
    console.log('Creating self-signed certificate...');
    var cert = forge.pki.createCertificate();
    // set_serial 01
    cert.serialNumber = '01';
    // -days 365
    cert.validity.notBefore = new Date();
    cert.validity.notAfter = new Date();
    cert.validity.notAfter.setFullYear(cert.validity.notBefore.getFullYear() + 1);
    // subject from CSR
    cert.setSubject(csr.subject.attributes);
    // issuer from CSR
    cert.setIssuer(csr.subject.attributes);
    // set basic extensions
    cert.setExtensions([
        {
            name: 'basicConstraints',
            cA: true
        },
        {
            name: 'keyUsage',
            keyCertSign: true,
            digitalSignature: true,
            nonRepudiation: true,
            keyEncipherment: true,
            dataEncipherment: true
        },
        {
            name: 'extKeyUsage',
            serverAuth: true,
            clientAuth: true,
            codeSigning: true,
            emailProtection: true,
            timeStamping: true
        },
        {
            name: 'nsCertType',
            client: true,
            server: true,
            email: true,
            objsign: true,
            sslCA: true,
            emailCA: true,
            objCA: true
        }
    ]);
    cert.publicKey = csr.publicKey;

    //self-sign certificate
    cert.sign(keys.privateKey);

    // PEM-format self-signed crt
    var crtPEM = forge.pki.certificateToPem(cert);

    // Log PEM-formatted self-signed crt, Uncomment code below if needed.
    // console.log('\nSelf-Signed Certification:');
    // console.log(crtPEM);

    // write file to config directory
    fs.writeFileSync(__dirname + '/config/selfSignedCertificate.pem', crtPEM);
    console.log('Certificate created...');

} catch (error) {
    console.log(error);
}

function requirePrompt(promptString) {
    var input = prompt(promptString);
    if (input === null || input === '') {
        console.log('Field was not given a value, please provide a value when prompted');
        input = prompt(promptString);
        if (input === null || input === '') {
            throw new Error('A field was not given a value');
        }
    }
    return input;
}