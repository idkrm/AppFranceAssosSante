const express = require('express');
const Donation = require('../models/Don');
const RecurringDonation = require('../models/DonRec');
const User = require('../models/User');
const Association = require('../models/Assos');

const router = express.Router();

// Route pour créer un don unique
router.post('/donations', async (req, res) => {
  try {
    const { montant, associationId, utilisateurEmail, typePaiement } = req.body;

    // Vérifie si l'utilisateur existe
    if(utilisateurEmail != null){
    const user = await User.findOne({email: utilisateurEmail});
        if (!user) {
          return res.status(400).json({ message: 'Utilisateur non trouvé' });
        }
    }

    const donation = new Don({
      montant,
      association: associationId,
      date: new Date(),
      utilisateurEmail,
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

// Route pour récupérer la liste des années avec des dons
router.get("/dons/annee", async (req, res) => {
  try {
      const years = await Donation.aggregate([
          {
              $group: {
                  _id: { $year: "$date" } // Extraire l'année de chaque don
              }
          },
          { $sort: { "_id": -1 } } // Trier par année décroissante
      ]);

      const result = years.map(y => y._id.toString()); // Convertir en liste de strings
      res.json(result);
  } catch (error) {
      console.error("Erreur lors de la récupération des années des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer la liste des années avec des dons
router.get("/dons_rec/annee", async (req, res) => {
  try {
      const years = await RecurringDonation.aggregate([
          {
              $group: {
                  _id: { $year: "$date" } // Extraire l'année de chaque don
              }
          },
          { $sort: { "_id": -1 } } // Trier par année décroissante
      ]);

      const result = years.map(y => y._id.toString()); // Convertir en liste de strings
      res.json(result);
  } catch (error) {
      console.error("Erreur lors de la récupération des années des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer le total des dons d'une année spécifique
router.get("/dons/total/:year", async (req, res) => {
  try {
      const year = parseInt(req.params.year);
      if (isNaN(year)) return res.status(400).json({ error: "Année invalide" });
      
      // Filtrer les dons qui appartiennent à l'année donnée
      const dons = await Donation.aggregate([
          {
              $match: {
                  date: {
                      $gte: new Date(`${year}-01-01T00:00:00.000Z`), //Début de l'année
                      $lt: new Date(`${year}-12-31T23:59:59.999Z`) //Fin de l'année
                  }
              }
          },
          {
              $group: {
                  _id: null,
                  total: { $sum: "$montant" } // Somme des montants des dons
              }
          }
      ]);

      if (dons.length > 0) {
          res.json({ total: dons[0].total });
      } else {
          res.json({ total: 0 });
      }
  } catch (error) {
      console.error("Erreur lors de la récupération du total des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});


// Route pour récupérer le total des dons récurrents d'une année spécifique
router.get("/dons/rec/total/:year", async (req, res) => {
  try {
      const year = parseInt(req.params.year);
      if (isNaN(year)) return res.status(400).json({ error: "Année invalide" });
      
      // Filtrer les dons qui appartiennent à l'année donnée
      const dons = await RecurringDonation.aggregate([
          {
              $match: {
                  date: {
                      $gte: new Date(`${year}-01-01T00:00:00.000Z`), //Début de l'année
                      $lt: new Date(`${year}-12-31T23:59:59.999Z`) //Fin de l'année
                  }
              }
          },
          {
              $group: {
                  _id: null,
                  total_rec: { $sum: "$montant" } // Somme des montants des dons
              }
          }
      ]);

      if (dons.length > 0) {
          res.json({ total_rec: dons[0].total });
      } else {
          res.json({ total_rec: 0 });
      }
  } catch (error) {
      console.error("Erreur lors de la récupération du total des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer la somme de dons récurrents par mois pour une année donnée
router.get("/dons/rec/mois/:year", async (req, res) => {
  try {
      const year = parseInt(req.params.year); // Convertir l'année en nombre
      if (isNaN(year)) return res.status(400).json({ error: "Année invalide" });
      
      // Agrégation MongoDB pour compter les dons par mois
      const donsParMois = await RecurringDonation.aggregate([
          {
              $match: {
                  date: {
                      $gte: new Date(`${year}-01-01T00:00:00.000Z`),
                      $lt: new Date(`${year}-12-31T23:59:59.999Z`)
                  }
              }
          },
          {
              $group: {
                  _id: { $month: "$date" }, // Regrouper par mois
                  //count: { $sum: 1 } // Compter le nombre de dons
                  total_mois: {$sum: "$montant"}
              }
          }
      ]);

      // Créer un objet avec les mois vides par défaut
      const result = {
          "Jan": 0, "Fev": 0, "Mar": 0, "Avr": 0, "Mai": 0, "Juin": 0,
          "Juil": 0, "Aout": 0, "Sep": 0, "Oct": 0, "Nov": 0, "Dec": 0
      };

      // Mapper les résultats de MongoDB aux mois correspondants
      const moisMap = {
          1: "Jan", 2: "Fev", 3: "Mar", 4: "Avr", 5: "Mai", 6: "Juin",
          7: "Juil", 8: "Aout", 9: "Sep", 10: "Oct", 11: "Nov", 12: "Dec"
      };

      //donsParMois.forEach(({ _id, count }) => {
      donsParMois.forEach(({ _id, total_mois }) => {
          result[moisMap[_id]] = total_mois;
      });

      res.json(result);
  } catch (error) {
      console.error("Erreur lors de la récupération des dons récurrents par mois:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer les dons par le mail de l'utilisateur
router.get("/dons/:email", async (req, res) => {
  try {
      const mail = req.params.email;

      // Trouver toutes les associations ayant ce filtre
      const donsMail = await Donation.find({ emailUtilisateur: mail }).populate("association");;

      if (donsMail.length === 0) {
        return res.status(404).json({ message: "Aucune association trouvée" });
      }

    res.status(200).json(donsMail);
  } catch (error) {
    console.error("Erreur serveur:", error);
    res.status(500).json({ message: "Erreur interne du serveur" });
}
});

// Route pour récupérer les dons récurrents par le mail de l'utilisateur
router.get("/donsrec/:email", async (req, res) => {
  try {
      const mail = req.params.email;

      // Trouver toutes les associations ayant ce filtre
      const donsRecMail = await RecurringDonation.find({ emailUtilisateur: mail }).populate("association");;

      if (donsRecMail.length === 0) {
        return res.status(404).json({ message: "Aucune association trouvée" });
      }

    res.status(200).json(donsRecMail);
  } catch (error) {
    console.error("Erreur serveur:", error);
    res.status(500).json({ message: "Erreur interne du serveur" });
}
});

module.exports = router;
