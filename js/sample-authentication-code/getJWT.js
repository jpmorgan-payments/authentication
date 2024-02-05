"use strict";
const fs = require('fs');
const jwt = require('jsonwebtoken');
const express = require('express');
const bodyParser = require('body-parser');
const upperCase = require('upper-case');
const forge = require('node-forge');
const config = require('./config/config.json');

const {pki, md} = forge;
const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

var payload1 = {
    jti: config.jti
};

// PublicKey
var certPath = config.certPath;

// PrivateKey
var privateKEY = fs.readFileSync(config.privateKey, 'utf8');

// Issuer
var i = config.client_id;
// Subject
var s = config.client_id;
// Audience
var a = config.audience;

// SIGNING OPTIONS...
var signOptions = {
    issuer: i,
    subject: s,
    audience: a,
    expiresIn: config.expiresIn,
    algorithm: config.algorithm,
    keyid: getCertThumbPrint(certPath)
};

var token = jwt.sign(payload1, privateKEY, signOptions);
var value = token.toString().replace('\n', '');
// Remove new lines for proper format
console.log("JWT: " + value);

// Get the ThumbPrint from Public Certificate using RSA
function getCertThumbPrint(pathName) {
    var pem = fs.readFileSync(pathName);
    var cert = forge.pki.certificateFromPem(pem);
    var asn1 = forge.pki.certificateToAsn1(cert);
    var der = forge.asn1.toDer(asn1);
    var sha1 = forge.md.sha1.create();
    sha1.update(der.bytes());
    var sha256 = forge.md.sha256.create();
    sha256.update(der.bytes());
    return upperCase(sha1.digest().toHex());
}