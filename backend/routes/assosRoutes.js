const express = require('express');
const Association = require('../models/Assos');

const router = express.Router();

// Récupérer toutes les associations
router.get('/assos', async (req, res) => {
  try {
    const associations = await Association.find();
    res.status(200).json(associations);
  } catch (error) {
    console.error('Erreur lors de la récupération des associations:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Récupérer une association par son ID
router.get('/assos/:id', async (req, res) => {
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

// Récupérer une association par son nom
router.get('/assos/:nom', async (req, res) => {
  try {
    const assosName = req.params.nom;  // Le nom passé dans les paramètres de l'URL

    // Recherche de l'association par nom
    const assos = await Association.findOne({ nom: assosName });

    if (!assos) {
      return res.status(404).json({ message: 'Association non trouvée' });
    }

    res.status(200).json(assos);  // Répond avec l'assos trouvée
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'association par nom:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Route pour récupérer les associations par filtre
router.get("/assos/:filtre", async (req, res) => {
  try {
      const filter = req.params.filtre;

      // Trouver toutes les associations ayant ce filtre
      const assosFiltre = await Association.find({ filtre: filter });
      /*
      if (assosFiltre.length === 0) {
        return res.status(404).json({ message: "Aucune association trouvée" });
      }
      */

    res.status(200).json(assosFiltre);
  } catch (error) {
    console.error("Erreur serveur:", error);
    res.status(500).json({ message: "Erreur interne du serveur" });
}
});

module.exports = router;
