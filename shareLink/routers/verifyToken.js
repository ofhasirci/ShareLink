var jwt = require('jsonwebtoken');
var keys = require('../config/keys');

function verifyToken(req, res, next) {
    var token = req.headers['x-access-token'];
    if (!token) return res.status(200).send({auth: false, message: 'No token provided'});
    //console.log("token:", token);

    jwt.verify(token, keys.tokenInfo.secret, (err, decoded) => {
        if (err) return res.status(200).send({auth: false, message: 'Failed to authenticate token.'});
    
        req.userId = decoded.id;
        //console.log("Verified token. UserId: ", req.userId);
        next();
    });
}

module.exports = verifyToken;