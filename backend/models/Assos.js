const mongoose = require('mongoose');

const associationSchema = new mongoose.Schema({
  nom: {
    type: String,
    required: true,
    unique: true,
  },
  logo: {
    type: String,
    required: true,
  },
});

const Association = mongoose.model('Association', associationSchema);

module.exports = Association;
