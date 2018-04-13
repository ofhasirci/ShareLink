var express = require('express');
var router = express.Router();
var bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const bcyrpt = require('bcryptjs');
const keys = require('../config/keys');
const mongoose = require('mongoose');
const User = require('../models/usermodel');
const VerifyToken = require('./verifyToken');
const randomstring = require('randomstring');
const nodemailer = require('nodemailer');

var urlencodedParser = bodyParser.urlencoded({extended: false});

// Sigin endpoint
router.post('/signin', urlencodedParser, (req, res) => {
    console.log(req.body.email, req.body.password);
    User.findOne({email: req.body.email}).then((user) => {
        if (user) return res.status(200).send({auth: false, message: 'This email is exist.', token: null});
        //console.log('user saving');
        var hashedPassword = bcyrpt.hashSync(req.body.password, 8);

        var user = new User({
            email: req.body.email,
            password: hashedPassword
        });
        user.save().then((record) => {
            var token = jwt.sign({id: record._id}, keys.tokenInfo.secret, {expiresIn: "30d"});

            res.status(200).send({auth: true, token: token, email: record.email, groups: record.groups});
        });

    });
    
});


//Get Profile via token
router.get('/profile', VerifyToken, (req, res) => {
    

    User.findById(req.userId, {password: 0}).then((user) => {
        if (!user) return res.status(200).send({auth: false, message: 'No user found'});
        console.log("responce are valid");
        res.status(200).send({auth: true, email: user.email, groups:user.groups});
    })
    .catch(err => res.status(200).send({auth: false, message: "There was a problem finding user."}));
    
});


//Login endpoint
router.post('/login', urlencodedParser, (req, res) => {
    User.findOne({email: req.body.email}).then((user) => {
        var isPassValid = bcyrpt.compareSync(req.body.password, user.password);
        if (!isPassValid) return res.status(200).send({auth: false, message: 'invalid password/email', token: null});

        var token = jwt.sign({id: user._id }, keys.tokenInfo.secret, {expiresIn: "30d"});

        res.status(200).send({auth:true, token: token, email: user.email, groups: user.groups});
    })
    .catch(err => res.status(200).send({auth:false, message: 'invalid password/email', token: null}));
});

//!!!!!!!!!!!!!!!!!!!!---Add Token-----!!!!!!!!!!!!!!
//Change Password
router.post('/changepass', urlencodedParser, (req, res) => {
    User.findOne({email: req.body.email}).then((user) => {
        var password = user.password;
        
        if (bcyrpt.compareSync(req.body.oldPassword, password)){
            var hashedPassword = bcyrpt.hashSync(req.body.newPassword, 8);
            user.password = hashedPassword;

            return user.save();
        } else {
            res.status(200).send({auth: false, message: 'Invalid old password'});
        }
    })
    .then(user => res.status(200).send({auth: true, message: 'Password updated successfully'}))
    .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));
});


//Reset Password
router.post('/resetpass', urlencodedParser, (req, res) => {
    var randompass = randomstring.generate(7);
    User.findOne({email: req.body.email}).then((user) => {
        if (!user) return res.status(200).send({auth: false, message: 'This email is not exist.'});

        var hashRandomPass = bcyrpt.hashSync(randompass, 8);

        user.password = hashRandomPass;

        return user.save();
    })
    .then(user => {
        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
              user: 'ofhasirci@gmail.com',
              pass: 'Eastanbulemirulma'
            }
          });

        var mailOptions = {
            from: "Share Link <ofhasirci@gmail.com>",
            to: req.body.email,
            subject: "Reset password request",
            html: `Hello, 
                    
                    <p>Your temprorary password is <b>${randompass}</b>
                    Please log in with it and set a new password from your profile settings.
                    
                    Thanks,
                    ShareLinks.</p>`
        };

        return transporter.sendMail(mailOptions);
    })
    .then(info => {
        console.log(info);
        res.status(200).send({auth: true, message: 'Check your email for instructions.'});
    })
    .catch(err => {
        console.log(err);
        res.status(200).send({auth: false, message: 'Internal server Error.'});
    });

});


module.exports = router;