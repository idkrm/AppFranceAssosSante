const express = require('express');
const User = require('../models/User');
const router = express.Router();

// Route pour créer un nouvel utilisateur
router.post('/register', async (req, res) => {
  try {
    const { username, email, password } = req.body;

    // Créer un nouvel utilisateur
    const newUser = new User({ username, email, password });
    await newUser.save();

    res.status(201).json({ message: 'Utilisateur créé', user: newUser });
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
    const { username, email, password } = req.body;
    const userId = req.params.id;  // L'ID de l'utilisateur à mettre à jour

    // Mise à jour de l'utilisateur
    const updatedUser = await User.findByIdAndUpdate(
      userId,  // ID de l'utilisateur à mettre à jour
      { username, email, password },  // Données à mettre à jour
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
router.delete('/delete/:id', async (req, res) => {
  try {
    const userId = req.params.id;  // L'ID de l'utilisateur à supprimer

    // Suppression de l'utilisateur
    const deletedUser = await User.findByIdAndDelete(userId);

    if (!deletedUser) {
      return res.status(404).json({ message: 'Utilisateur non trouvé' });
    }

    res.status(200).json({ message: 'Utilisateur supprimé' });
  } catch (error) {
    console.error('Erreur lors de la suppression de l\'utilisateur:', error);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});


module.exports = router;
