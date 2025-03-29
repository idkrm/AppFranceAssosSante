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

// Récupérer l'ID d'une association par son nom
router.get("/association/id/:nom", async (req, res) => {
  try {
    const nom = req.params.nom;
    let nomDecode;
    try {
      nomDecode = decodeURIComponent(req.params.nom.replace(/\+/g, ' '));
    } catch (e) {
      console.error("Erreur de décodage du nom:", e);
      return res.status(400).json({ error: "Nom d'association mal formaté" });
    }
    // 2. Nettoyage supplémentaire (supprimer les espaces multiples/traînants)
    const nomNettoye = nomDecode.trim().replace(/\s+/g, ' ');

    // 3. Création de la regex en échappant les caractères spéciaux
    //const motifRegex = nomNettoye.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    //const regex = new RegExp(`^${motifRegex}$`, "i");

    //const association = await Association.findOne({ nom: regex });
    const association = await Association.findOne({ nom: nomNettoye });

    if (!association) {
      //console.log(`Association avec le nom ${regex} non trouvé`);
      console.log(`Association avec le nom ${nomNettoye} non trouvé`);
      return res.status(404).json({ error: "Association non trouvée" });
    }

    //console.log(`Association avec le nom ${regex} a pour id: ${association._id}`);
    console.log(`Association avec le nom ${nomNettoye} a pour id: ${association._id}`);
    res.status(200).json({id: association._id });
  } catch (error) {
    console.error("Erreur lors de la récupération de l'ID de l'association:", error);
    res.status(500).json({ error: "Erreur serveur" });
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
