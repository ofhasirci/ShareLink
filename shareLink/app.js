const mongoose = require('mongoose');
const Link = require('./models/linkmodel');
const User = require('./models/usermodel');
const Group = require('./models/groupmodel');
var express = require('express');
var socket = require('socket.io');
var AuthControllers = require('./routers/AuthControllers');
var LinkControllers = require('./routers/LinkControllers');
const keys = require('./config/keys');

//Use ES6 Promise for MongoDB
mongoose.Promise = global.Promise;


var app = express();

app.use('/auth', AuthControllers);
app.use('/link', LinkControllers);


//MongoDB connection
mongoose.connect(keys.mongodb.dbURI);
mongoose.connection.once('open', function(){
    console.log('Connection has been made, now make fireworks...');
}).on('error', function(error){
    console.log('Connection error: ', error);
});

//Server
var server = app.listen(3000, function(){
    console.log('Port 3000 is listening');
});

//Socket setup
var io = socket(server);
// Socket connection
io.on('connection', function(socket){
    console.log('Socket connection established', socket.id);
    
    socket.on('joinGroup', function(groupName){
        socket.join(groupName);
        console.log("groupname: " + groupName);
        console.log("rooms" + socket.rooms);
    });

    socket.on('shareLink', (data) => {
        var linkData = {
            user: data.userName,
            link: data.link,
            description: data.description,
            date: data.date
        };

        Group.findOne({name: data.groupName}).then(group => {
            console.log("group name ",  data.groupName);
            //console.log("data",  data);
            group.links.push(linkData);
            group.save();
        });

        io.sockets.to(data.groupName).emit('shareLink', linkData);
    });

    socket.on('leaveGroup', (groupName) => {
        socket.leave(groupName, (err) => {
            if (err) console.log(err);
            console.log("disconnection", socket.id);
        });
    });

    /*socket.on('entry', function(data){
        var linkObject = new Link({
            user: data.user,
            link: data.link,
            description: data.description,
            date: data.date
        });
        linkObject.save();

        io.sockets.emit('entry', data);
    });*/

});



