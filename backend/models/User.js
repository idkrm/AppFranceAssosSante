const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  email: {
      type: String,
      required: true,
      unique: true,
  },
  mdp: {
    type: String,
    required: true,
  },
  civilite: {
    type: String,
    enum: ['M', 'F','Autre'],
    required: true,
  },
  nom: {
    type: String,
    required: true,
  },
  prenom: {
    type: String,
    required: true,
  },
  handicap: {
    type: String,
    default: null, // Champs optionnel
  },
  role: {
    type: String,
    enum: ['utilisateur', 'administrateur'],
    required: true,
  },
});

const User = mongoose.model('User', userSchema);

module.exports = User;
