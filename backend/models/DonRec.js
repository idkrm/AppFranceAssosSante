const mongoose = require('mongoose');

const recurringDonationSchema = new mongoose.Schema({
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
  utilisateur: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
      required: true,
    },
  typePaiement: {
    type: String,
    enum: ['CB', 'PayPal', 'Virement', 'Chèque'],
    required: true,
  },
  frequence: {
    type: String,
    enum: ['mensuel', 'annuel'],
    required: true,
  },
  dateFin: {
    type: Date,
    required: true,
  },
});

const RecurringDonation = mongoose.model('RecurringDonation', recurringDonationSchema);

module.exports = RecurringDonation;
