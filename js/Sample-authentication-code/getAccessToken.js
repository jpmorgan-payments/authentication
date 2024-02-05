"use strict";
const fs = require('fs');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const express = require('express');
const bodyParser = require('body-parser');
const qs = require('querystring');
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
let access_token = '';

var payload = {
    client_id: config.client_id,
    client_assertion_type: config.client_assertion_type,
    client_assertion: token,
    grant_type: config.grant_type,
    resource: config.resource_id
};

const options = {
    headers: {
        'Content-Type': config.content_type
    },
	proxy: false
};

let url = config.ida_url;

axios.post(url, qs.stringify(payload), options).then(resp => {
    access_token = resp.data.access_token;
    console.log(JSON.stringify(resp.data, null, 2));
}).catch((err) => {
    console.log("Error ===> ", err);
});

// get the ThumbPrint from Public Certificate using RSA
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