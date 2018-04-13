const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Create Schema and Model

const linkmodelSchema = new Schema({
    user: String,
    link: String,
    description: String,
    date: String
});


const Link = mongoose.model('link', linkmodelSchema);

module.exports = Link;