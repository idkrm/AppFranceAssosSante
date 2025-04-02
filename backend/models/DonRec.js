const mongoose = require('mongoose');

const recurringDonationsSchema = new mongoose.Schema({
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
  utilisateurEmail: {
      type: String, // Référence à l'utilisateur'
      ref: 'User',
      required: true,
    },
  typePaiement: {
    type: String,
    enum: ['CB', 'PayPal', 'GooglePay'],
    required: true,
  },
  frequence: {
    type: String,
    enum: ['Mensuel', 'Annuel',
      'Monthly', 'Annually',
      '每月', '每年'],
    required: true,
  },
  dateFin: {
    type: Date,
    required: true,
  },
});

const RecurringDonations = mongoose.model('RecurringDonations', recurringDonationsSchema);

module.exports = RecurringDonations;
