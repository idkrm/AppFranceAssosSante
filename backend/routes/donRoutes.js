const express = require('express');
const mongoose = require('mongoose');
const Donation = require('../models/Don');
const RecurringDonations = require('../models/DonRec');
const User = require('../models/User');
const Association = require('../models/Assos');

const router = express.Router();

// Route pour créer un don unique
router.post('/donations', async (req, res) => {
  try {
    const { montant, date, utilisateurEmail, associationNom, typePaiement,  } = req.body;
     console.log("Don reçu :", req.body);
    // Vérifie si l'utilisateur existe
    if(utilisateurEmail != null){
    const user = await User.findOne({email: utilisateurEmail});
        if (!user) {
            console.error("user non trouvée :", user);
          return res.status(400).json({ message: 'Utilisateur non trouvé' });
        }
    }

    const associationData = await Association.findOne({ nom: req.body.association });
    if (!associationData) {
        console.error("Association non trouvée :", associationData);
          return res.status(400).json({ message: "Association non trouvée" });
        }

    const donation = new Donation({
      montant,
      association: new mongoose.Types.ObjectId(associationData._id),
      date: new Date(date),
      emailUtilisateur: utilisateurEmail,
      typePaiement,
    });

    await donation.save();
    res.status(201).json({ message: 'Don unique créé avec succès', donation });
  } catch (error) {
    console.error('Erreur lors de la création du don :', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

//pour verifier si le don recurrent existe deja
const checkRecurringDonation = async (utilisateurEmail, associationId, frequence) => {
  try {
    return await RecurringDonations.findOne({
      emailUtilisateur: utilisateurEmail,
      association: associationId,
      frequence: frequence
    });
  } catch (error) {
    console.error('Erreur lors de la vérification du don récurrent :', error);
    throw error; // Propage l'erreur pour qu'elle soit gérée par le catch de la route
  }
};

// Route pour créer un don récurrent
router.post('/recurring-donations', async (req, res) => {
  try {
    const { montant, associationNom, utilisateurEmail, typePaiement, frequence, dateFin } = req.body;
    console.log("Don reçu :", req.body);

    // Vérifie si l'utilisateur existe
    const user = await User.findOne({email : utilisateurEmail});

    if (!user) {
      return res.status(400).json({ message: 'Utilisateur non trouvé' });
    }
    const associationData = await Association.findOne({ nom: req.body.association });
        if (!associationData) {
              return res.status(400).json({ message: "Association non trouvée" });
        }

    // Vérifie si un don récurrent de ce type existe déjà pour cette association et cet utilisateur
    const existingDonation = await checkRecurringDonation(utilisateurEmail, new mongoose.Types.ObjectId(associationData._id), frequence);
    if (existingDonation) {
      return res.status(400).json({ message: 'Un don récurrent existe déjà pour cette association et cette fréquence.' });
    }

    const recurringDonation = new RecurringDonations({
      montant,
      association: new mongoose.Types.ObjectId(associationData._id),
      date: new Date(),
      utilisateurEmail,
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
router.get("/dons/annee/:assosId", async (req, res) => {
  try {
    const associationId = req.params.assosId;
    if (!mongoose.Types.ObjectId.isValid(associationId)) {
      return res.status(400).json({ error: "ID d'association invalide" });
    }

      const years = await Donation.aggregate([
        {
          $match: {
            association: new mongoose.Types.ObjectId(associationId) // Filtre les dons par association
          }
        },
        {
          $group: {
            _id: { $year: "$date" } // Extrait l'année de chaque date de don
          }
        },
        {
          $sort: { "_id": -1 } // Trie par année décroissante
        },
        {
          $project: {
            year: "$_id", // Renomme _id en year
            _id: 0        // Supprime le champ _id
          }
        }
      ]);

      // 3. Transformation en tableau simple d'années

      const result = years.map(y => y.year.toString()); // Convertir en liste de strings
      res.json(result);
  } catch (error) {
      console.error("Erreur lors de la récupération des années des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer la liste des années avec des dons
router.get("/dons_rec/annee/:assosId", async (req, res) => {
  try {
    const associationId = req.params.assosId;
    if (!mongoose.Types.ObjectId.isValid(associationId)) {
      return res.status(400).json({ error: "ID d'association invalide" });
    }
    const years = await RecurringDonations.aggregate([
      {
        $match: {
          association: new mongoose.Types.ObjectId(associationId),
          $or: [
            { date: { $exists: true, $ne: null } },
            { dateFin: { $exists: true, $ne: null } }
          ]
        }
      },
      {
        $project: {
          startYear: { $year: "$date" },
          endYear: { $year: "$dateFin" }
        }
      },
      {
        $group: {
          _id: null,
          startYears: { $addToSet: "$startYear" },
          endYears: { $addToSet: "$endYear" }
        }
      },
      {
        $project: {
          allYears: {
            $setUnion: ["$startYears", "$endYears"]
          }
        }
      },
      { $unwind: "$allYears" },
      { $sort: { allYears: -1 } },
      {
        $group: {
          _id: null,
          years: { $push: "$allYears" }
        }
      }
    ]);

    const result = years.length > 0 ? years[0].years : [];
    res.status(200).json(result);

  } catch (error) {
      console.error("Erreur lors de la récupération des années des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer les dons d'une association
router.get("/dons/:assosID", async (req, res) => {
  try {
    const assos = req.params.assos;

    // Vérifier si l'ID est valide
    if (!mongoose.Types.ObjectId.isValid(assosID)) {
      return res.status(400).json({ error: "ID d'association invalide" });
    }

    // Filtrer les dons qui appartiennent à l'association
    const donsList = await Donation.find({association: assosID})

    /*
    if (donsList.length === 0) {
      return res.status(404).json({ message: "Aucune don trouvé pour cette association" });
    }
    */

    res.status(200).json(donsList);
  } catch (error) {
      console.error("Erreur lors de la récupération des dons de l'association:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer le total des dons d'une année spécifique
router.get("/dons/total/:assosId/:year", async (req, res) => {
  try {
      const year = parseInt(req.params.year);
      if (isNaN(year)) return res.status(400).json({ error: "Année invalide" });
      
      const associationId = req.params.assosId;
      if (!mongoose.Types.ObjectId.isValid(associationId)) {
        return res.status(400).json({ error: "ID d'association invalide" });
      }

      // Filtrer les dons qui appartiennent à l'année donnée
      const dons = await Donation.aggregate([
        {
          $match: {
              association: new mongoose.Types.ObjectId(associationId),
              date: { $exists: true, $ne: null }
          }
      },
      {
          $addFields: {
              year: { $year: "$date" } // Extraction de l'année
          }
      },
      {
          $match: {
              year: year // Filtre sur l'année demandée
          }
      },
      {
          $group: {
              _id: null,
              total: { $sum: "$montant" }
          }
      }
      ]);
      //console.log(`Résultat agrégation pour ${year}:`, dons);

      res.json({total: dons[0]?.total || 0.0});
  } catch (error) {
      console.error("Erreur lors de la récupération du total des dons:", error);
      res.status(500).json({ error: "Erreur serveur" });
  }
});

// Route pour récupérer les dons par le mail de l'utilisateur
router.get("/dons/user/:email", async (req, res) => {
  try {
      const mail = req.params.email;

      // Trouver toutes les associations ayant ce filtre
      const donsMail = await Donation.find({ emailUtilisateur: mail }).populate("association");

      if (donsMail.length === 0) {
        return res.status(404).json({ message: "Aucune association trouvée" });
      }

      //console.log(`Résultat agrégation pour ${mail}:`, donsMail);

    res.status(200).json(donsMail);
  } catch (error) {
    console.error("Erreur serveur:", error);
    res.status(500).json({ message: "Erreur interne du serveur" });
}
});

// Route pour récupérer les dons récurrents par le mail de l'utilisateur
router.get("/donsrec/user/:email", async (req, res) => {
  try {
      const mail = req.params.email;
      //console.log("📩 Email reçu:", mail);

      // Vérifier si l'email est bien récupéré
      if (!mail) {
          console.error("⚠️ Erreur: Email non fourni !");
          return res.status(400).json({ message: "Email manquant dans la requête" });
      }

      // Chercher les dons récurrents pour cet email
      const donsRecMail = await RecurringDonations.find({ utilisateurEmail: mail }).populate("association");
      //console.log("📢 Dons trouvés:", donsRecMail);

      // Vérifier si la requête retourne bien des résultats
      if (donsRecMail.length === 0) {
          console.warn("⚠️ Aucun don trouvé pour cet utilisateur.");
          return res.status(404).json({ message: "Aucun don trouvé" });
      }

      // Retourner les dons trouvés
      res.status(200).json(donsRecMail);

  } catch (error) {
      console.error("❌ Erreur serveur:", error);
      res.status(500).json({ message: "Erreur interne du serveur" });
  }
});

router.put('/deleteDonRecurrent', async (req, res) => {
  try {
    const { emailUser, frequence, assos } = req.body;
    console.log(`Body deleteDonRecurrent : ${req.body}`)

    // Récupération de l'association
    const assoDuDon = await Association.findOne({ nom: assos });
    console.log(`Association trouvée : ${assoDuDon}`);

    if (!assoDuDon) {
      console.log(`Association avec le nom ${assos} non trouvée.`);
      return res.status(404).json({ message: "Association non trouvée" });
    }

    // Vérification de l'existence du don récurrent
    const donation = await RecurringDonations.findOne({
      association: new mongoose.Types.ObjectId(assoDuDon._id),
      frequence: frequence,
      utilisateurEmail: emailUser
    });
    console.log(`Don récurrent trouvé : ${donation}`);

    if (!donation) {
      console.log(`Don récurrent avec frequence ${frequence} pour l'utilisateur ${emailUser} et l'association ${assos} non trouvé.`);
      return res.status(404).json({ message: "Don récurrent non trouvé" });
    }

    // Mise à jour de la date de fin
    donation.dateFin = new Date();
    console.log(`Mise à jour de la date de fin : ${donation.dateFin}`);

    // Sauvegarde de la modification
    await donation.save();
    console.log('Don récurrent supprimé avec succès');

    // Réponse
    res.status(200).json({ message: "Suppression du don !", donation });
  } catch (error) {
    console.error("Erreur lors de la mise à jour de la date :", error);
    res.status(500).json({ message: "Erreur du serveur" });
  }
});

// Fonction pour calculer les dons récurrents
async function calculerDonsRecurrent(assosID, year, month) {
  try {
      const dons = await RecurringDonations.find({ association: new mongoose.Types.ObjectId(assosID) }).lean();
      console.log("Nombre de dons récurrents trouvés:", dons.length);

      let totalMensuel = 0;
      let totalAnnuel = 0;
      let countMensuel = 0;
      let countAnnuel = 0;

      const anneeSelectionnee = parseInt(year, 10);
      const moisSelectionne = parseInt(month, 10) - 1;
      const current = new Date(Date.UTC(anneeSelectionnee, moisSelectionne + 1, 0));  //dernier jour du mois, pour prendre en compte tout le mois actuel

      dons.forEach(don => {
        try {
          const { montant, frequence, date, dateFin } = don;
          console.log(`Traitement d'un don : montant=${montant}, frequence=${frequence}, date=${date}, dateFin=${dateFin}`);
          const debut = new Date(date);
          const fin = new Date(dateFin);

          console.log("current:", current, "debut:", debut, "fin:", fin);
          console.log("current >= debut ?", current >= debut);
          console.log("current <= fin ?", current <= fin);

          if (current.getFullYear >= debut.getFullYear && current.getFullYear <= fin.getFullYear) {
            if (frequence === "Annuel" ){
              // Vérifier si le don doit être compté cette année
              if (debut.getFullYear() <= anneeSelectionnee) {
                totalAnnuel += montant;
                countAnnuel++;

                // Ajouter au total mensuel uniquement le mois de renouvellement
                if (debut.getMonth() === moisSelectionne) {
                    totalMensuel += montant;
                    countMensuel++;
                }
              }
            }
            else if (frequence === "Mensuel") {
              // Calcul du nombre de mois actifs dans l'année sélectionnée
              const debutAnnee = new Date(anneeSelectionnee, 0, 1); // 1er janvier de l'année sélectionnée
              const finAnnee = new Date(anneeSelectionnee, 11, 31); // 31 décembre de l'année sélectionnée

              const debutEffectif = debut > debutAnnee ? debut : debutAnnee; // Premier mois du don dans l'année
              const finEffectif = fin < finAnnee ? fin : finAnnee; // Dernier mois du don dans l'année

              const moisDansAnnee = 
              (finEffectif.getFullYear() - debutEffectif.getFullYear()) * 12 + 
              (finEffectif.getMonth() - debutEffectif.getMonth() + 1);

              if (moisDansAnnee > 0) {
                  totalAnnuel += montant * moisDansAnnee;
                  countAnnuel += moisDansAnnee;
              }
            }
          }
          if (current >= debut && current <= fin) {
            if (frequence === "Mensuel") {
              totalMensuel += montant;
              countMensuel++;
            }
          }
        } catch (error) {
          console.error("Erreur lors du traitement d'un don:", error);
        }
      });

      return { totalMensuel, countMensuel, totalAnnuel, countAnnuel };
  } catch (error) {
      console.error("Erreur lors du calcul des dons récurrents:", error);
      throw error;
  }
}

// Route API pour récupérer les dons récurrents par mois et année
router.get("/dons/recurrents/:assosID/:year/:month", async (req, res) => {
  try {
      const { assosID, year, month } = req.params;
      console.log("Paramètres reçus :", year, month);
      const data = await calculerDonsRecurrent(assosID, year, month);

      
      console.log(`Résultat dons pour ${year}:`, data);
      res.json({ success: true, data });
  } catch (error) {
      res.status(500).json({ success: false, message: "Erreur serveur" });
  }
});


module.exports = router;
