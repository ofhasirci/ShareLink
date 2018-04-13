var express = require('express');
var router = express.Router();
const mongoose = require('mongoose');
const Link = require('../models/linkmodel');
var bodyParser = require('body-parser');
const keys = require('../config/keys');
const VerifyToken = require('./verifyToken');
const Group = require('../models/groupmodel');
const User = require('../models/usermodel');

var urlencodedParser = bodyParser.urlencoded({extended: false});

//Get the records from db
router.get('/getlinks', VerifyToken, function(req, res){
    var limit = parseInt(req.query.limit);
    var skip = parseInt(req.query.skip)*limit;

    Group.findOne({name: req.query.groupName}).then(group => {
        res.status(200).send({name: group.name, starter: group.starter, dateOfStart: group.dateOfStart, members: group.members, links: group.links});
    });

    /*Link.find().limit(limit).skip(skip).sort({_id: -1}).select('user link description date -_id').then(function(records){
        var jsonRecords = JSON.stringify(records);
        //var info = JSON.parse(jsonRecords);
        //console.log(records);
        res.send(jsonRecords);
    });*/
    
});

//Save record to db
router.post('/sharelink', urlencodedParser, function(req, res){
    var jsonObject = JSON.stringify(req.body);
    var info = JSON.parse(jsonObject);
    
    var linkObject = new Link({
        user: info.user,
        link: info.link,
        description: info.description,
        date: info.date
    });

    linkObject.save().then(function(){
        console.log(info);
        res.end('They are saved..');
    });

});


//Create a Group 
router.post('/creategroup', urlencodedParser, (req, res) => {
    actualGroupName = req.body.groupName + '_' + req.body.starter;
    console.log("actual group name: " + actualGroupName);

    var group = new Group({
        name: actualGroupName,
        starter: req.body.starter,
        dateOfStart: req.body.dateOfStart,
        members: [req.body.starter]
    });

    group.save().then(record => {
        User.findOne({email: req.body.starter}).then(user => {
            user.groups.push(actualGroupName);
            user.save().then(record => {
                if (record) return res.status(200).send({auth: true});
            })
            .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));
        })
        .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));
    })
    .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));


});


//Add a contact to group
router.post('/addcontact', urlencodedParser, (req, res) => {
    User.findOne({email: req.body.email}).then(user => {
        if (!user) return res.status(200).send({auth: false, message: 'Sorry there is no such User!'});

        user.groups.push(req.body.groupName);
        user.save();
        
        Group.findOne({name: req.body.groupName}).then(group => {
            if (!group) return res.status(200).send({auth: false, message: 'Internal server Error!'});

            group.members.push(req.body.email);
            group.save().then(record => {
                res.status(200).send({auth: true});
            })
            .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));
        });
    })
    .catch(err => res.status(200).send({auth: false, message: 'Internal server Error!'}));
});



module.exports = router;