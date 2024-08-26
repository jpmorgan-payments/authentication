var jwt = require('jsonwebtoken');

function generate_digital_signature(digital_key, body) {
    return jwt.sign(body, digital_key, { algorithm: 'RS256' });
}

module.exports = generate_digital_signature;