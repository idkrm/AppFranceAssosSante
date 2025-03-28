const mongoose = require('mongoose');

const donationSchema = new mongoose.Schema({
  montant: {
    type: Number,
    required: true,
  },
  association: {
    type: mongoose.Schema.Types.ObjectId, // Référence à l'association
    ref: 'Assos',
    required: true,
  },
  date: {
    type: Date,
    required: true,
  },
  utilisateurEmail: {
    type: String?
  },
  typePaiement: {
    type: String,
    enum: ['CB', 'PayPal', 'GooglePay'],
    required: true,
  },
});

const Donation = mongoose.model('Donation', donationSchema);

module.exports = Donation;
