const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const groupSchema = new Schema({
    name: String,
    starter: String,
    dateOfStart: String,
    dateOfEnd: String,
    members: [String],
    links: [{
        user: String,
        link: String,
        description: String,
        date: String
    }]

});

const Group = mongoose.model('group', groupSchema);

module.exports = Group;