const express = require('express');
const Association = require('../models/Assos');

const router = express.Router();

// Récupérer toutes les associations
router.get('/associations', async (req, res) => {
  try {
    const associations = await Association.find();
    res.status(200).json(associations);
  } catch (error) {
    console.error('Erreur lors de la récupération des associations:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Récupérer une association par son ID
router.get('/associations/:id', async (req, res) => {
  try {
    const association = await Association.findById(req.params.id);

    if (!association) {
      return res.status(404).json({ message: 'Association non trouvée' });
    }

    res.status(200).json(association);
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'association:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});


module.exports = router;
