const express = require('express');
const mongoose = require('mongoose');
const Association = require('./models/Assos');

const userRoutes = require('./routes/userRoutes');
const assosRoutes = require('./routes/assosRoutes');
const donRoutes = require('./routes/donRoutes');

require('dotenv').config();
const app = express(); // crée le serveur express qui va config les routes
app.use(express.json()); // transforme les requetes http au format json

// Connexion à MongoDB
const uri = process.env.MONGO_URI;
mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => {
    console.log('Connecté à MongoDB');
  })
  .catch((err) => {
    console.error('Erreur de connexion à MongoDB:', err);
  });


// Utiliser les routes pour les utilisateurs
app.use('/users', userRoutes);
app.use('/associations', assosRoutes);
app.use('/donations', donRoutes);

// Démarrer le serveur
const PORT = 5000;
app.listen(PORT, () => {
  console.log(`Serveur en écoute sur le port ${PORT}`);
});

