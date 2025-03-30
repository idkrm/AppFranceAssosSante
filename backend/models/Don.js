const mongoose = require('mongoose');

const donationSchema = new mongoose.Schema({
  montant: {
    type: Number,
    required: true,
  },
  association: {
    type: mongoose.Schema.Types.ObjectId, // Référence à l'association
    ref: 'Association',
    required: true,
  },
  date: {
    type: Date,
    required: true,
  },
  emailUtilisateur: {
    type: mongoose.Schema.Types.ObjectId, // Référence à l'utilisateur
    ref: 'User',
  },
  typePaiement: {
    type: String,
    enum: ['CB', 'PayPal', 'GooglePay','ApplePay'],
    required: true,
  },
});

const Donation = mongoose.model('Donation', donationSchema);

module.exports = Donation;
