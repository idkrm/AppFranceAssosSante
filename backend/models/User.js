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
    enum: ['Monsieur', 'Madame', 'Ne souhaite pas répondre', 'Autre',
      '先生', '女士', '不想回答', '其他',
      'Mr', 'Mrs', 'Do not wish to answer', 'Other'],
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
    required: true,
  },
  admin: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Association',
    default: null, // Champs optionnel
  },
});

const User = mongoose.model('User', userSchema);

module.exports = User;
