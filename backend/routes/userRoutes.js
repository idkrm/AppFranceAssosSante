const express = require('express');
const User = require('../models/User');
const router = express.Router();

router.post('/register', async (req, res) => {
  try {
    const { nom, prenom, email, mdp, civilite, handicap } = req.body;

    // Vérifier si un utilisateur avec cet email existe déjà
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: 'Un utilisateur avec cet email existe déjà.' });
    }

    // Définir le rôle comme "utilisateur" pour tous les nouveaux enregistrements
    const role = 'utilisateur';  // Rôle par défaut pour les nouveaux utilisateurs

    // Créer un nouvel utilisateur avec le rôle par défaut "utilisateur"
    const newUser = new User({ nom, prenom, email, mdp, civilite, handicap, role });

    // Sauvegarder l'utilisateur dans la base de données
    await newUser.save();

    // Répondre avec une confirmation de la création de l'utilisateur
    res.status(201).json({ message: 'Utilisateur créé avec succès', user: newUser });
  } catch (error) {
    console.error('Erreur lors de la création de l\'utilisateur:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Route pour obtenir tous les utilisateurs
router.get('/users', async (req, res) => {
  try {
    const users = await User.find();
    res.status(200).json(users);
  } catch (error) {
    console.error('Erreur lors de la récupération des utilisateurs:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Route pour mettre à jour un utilisateur
router.put('/update/:id', async (req, res) => {
  try {
    const { nom, prenom, email, password, civilite, handicap } = req.body;
    const userId = req.params.id;  // L'ID de l'utilisateur à mettre à jour

    // Mise à jour de l'utilisateur
    const updatedUser = await User.findByIdAndUpdate(
      userId,  // ID de l'utilisateur à mettre à jour
      { nom, prenom, email, password },  // Données à mettre à jour
      { new: true }  // Retourne le nouvel utilisateur mis à jour
    );

    if (!updatedUser) {
      return res.status(404).json({ message: 'Utilisateur non trouvé' });
    }

    res.status(200).json({ message: 'Utilisateur mis à jour', user: updatedUser });
  } catch (error) {
    console.error('Erreur lors de la mise à jour de l\'utilisateur:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});

// Route pour supprimer un utilisateur
router.delete('/delete', async (req, res) => {
    console.log("Requête DELETE reçue");
  try {
    const { email } = req.body; // Destructuration pour extraire l'email

    if (!email) {
      return res.status(400).json({ message: 'Email requis' });
    }

    // Suppression de l'utilisateur
    const deletedUser = await User.findOneAndDelete({ email: email });

    if (!deletedUser) {
      return res.status(404).json({ message: 'Utilisateur non trouvé' });
    }

    res.status(200).json({
      message: 'Utilisateur supprimé',
      deletedUser: {
        email: deletedUser.email,
        id: deletedUser._id
      }
    });
  } catch (error) {
    console.error('Erreur lors de la suppression:', error);
    res.status(500).json({
      message: 'Erreur du serveur',
      error: error.message
    });
  }
});

// Route pour récupérer un utilisateur par email
router.get('/user/:email', async (req, res) => {
  try {
    const userEmail = req.params.email;  // L'email passé dans les paramètres de l'URL

    // Recherche de l'utilisateur par email
    const user = await User.findOne({ email: userEmail });

    if (!user) {
      return res.status(404).json({ message: 'Utilisateur non trouvé' });
    }

    res.status(200).json(user);  // Répond avec l'utilisateur trouvé
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'utilisateur par email:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});



module.exports = router;
