const express = require('express');
const mongoose = require('mongoose');
const Association = require('./models/Assos');
const cors = require('cors');
const QRCode = require('qrcode');

const userRoutes = require('./routes/userRoutes');
const assosRoutes = require('./routes/assosRoutes');
const donRoutes = require('./routes/donRoutes');

require('dotenv').config();
const app = express(); // crée le serveur express qui va config les routes
app.use(express.json()); // transforme les requetes http au format json
app.use(cors()); //debloque les requêtes venant d’autre site (elles sont bloquées par defaut)

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

//code qr
// const deepLinkUrl = "monapp://deeplink/page";  //deep link de l'app
// const playStoreUrl = "https://play.google.com/store/apps";  //url du play store

// app.get("/redirect", (req, res) => {
//   console.log("Redirection demandée !");
//   res.send(`
//     <html>
//       <head>
//         <title>Redirection...</title>
//         <script>
//           window.location.href = "${deepLinkUrl}";
//           setTimeout(() => { window.location.href = "${playStoreUrl}"; }, 2000);
//         </script>
//       </head>
//       <body>
//         <p>Si la redirection ne fonctionne pas, <a href="${playStoreUrl}">cliquez ici</a>.</p>
//       </body>
//     </html>
//   `);
// });

// const serverRedirectUrl = "http://10.10.43.117:5000/redirect"; //mettre l'ip de la machine

// QRCode.toFile("qrcodet.png", serverRedirectUrl, (err) => {
//   if (err) throw err;
//   console.log("QR Code généré !");
// });

// Démarrer le serveur
const PORT = 5000;
app.listen(PORT, () => {
  console.log(`Serveur en écoute sur le port ${PORT}`);
});