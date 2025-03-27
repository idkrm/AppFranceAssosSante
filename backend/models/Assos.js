const mongoose = require('mongoose');

const associationSchema = new mongoose.Schema({
  acronyme: {
    type: String,
    required: true,
  },
  filtre: {
    type: String,
    required: true,
  },
  description: {
      type: String,
    },
  img: {
      type: String,
    },
  nom: {
      type: String,
      required: true,
      unique : true,
    },
});

const Association = mongoose.model('Association', associationSchema);

module.exports = Association;
