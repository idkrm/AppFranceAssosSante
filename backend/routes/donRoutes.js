const express = require('express');
const Donation = require('../models/Donation');
const RecurringDonation = require('../models/RecurringDonation');
const User = require('../models/User');
const Association = require('../models/Association');

const router = express.Router();

// Route pour créer un don unique
router.post('/donations', async (req, res) => {
  try {
    const { montant, associationId, utilisateurId, typePaiement } = req.body;

    // Vérifie si l'utilisateur existe
    const user = await User.findById(utilisateurId);
    if (!user) {
      return res.status(400).json({ message: 'Utilisateur non trouvé' });
    }

    const donation = new Don({
      montant,
      association: associationId,
      date: new Date(),
      utilisateurId,
      typePaiement,
    });

    await donation.save();
    res.status(201).json({ message: 'Don unique créé avec succès', donation });
  } catch (error) {
    console.error('Erreur lors de la création du don :', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Route pour créer un don récurrent
router.post('/recurring-donations', async (req, res) => {
  try {
    const { montant, associationId, utilisateurId, typePaiement, frequence, dateFin } = req.body;

    // Vérifie si l'utilisateur existe
    const user = await User.findById(utilisateurId);

    if (!user) {
      return res.status(400).json({ message: 'Utilisateur non trouvé' });
    }

    // Vérifie si un don récurrent de ce type existe déjà pour cette association et cet utilisateur
    const existingDonation = await checkRecurringDonation(utilisateurId, associationId, frequence);
    if (existingDonation) {
      return res.status(400).json({ message: 'Un don récurrent existe déjà pour cette association et cette fréquence.' });
    }

    const recurringDonation = new DonRec({
      montant,
      association: associationId,
      date: new Date(),
      utilisateur: utilisateurId,
      typePaiement,
      frequence,
      dateFin,
    });

    await recurringDonation.save();
    res.status(201).json({ message: 'Don récurrent créé avec succès', recurringDonation });
  } catch (error) {
    console.error('Erreur lors de la création du don récurrent :', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

module.exports = router;
