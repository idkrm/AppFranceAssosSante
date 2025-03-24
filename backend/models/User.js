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
  admin: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Assos',
    default: null, // Champs optionnel
  },
});

const User = mongoose.model('User', userSchema);

module.exports = User;
