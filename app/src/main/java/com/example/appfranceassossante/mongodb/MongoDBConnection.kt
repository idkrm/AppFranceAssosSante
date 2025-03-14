package com.example.appfranceassossante.mongodb
import com.example.appfranceassossante.Utilisateur
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Indexes
import org.bson.Document
import java.util.Date

class MongoDBConnection{
    private val mongoDbPassword = "unmdppourmongodb" // à cacher !!!
    private val CONNECTION_STRING = "mongodb+srv://luciole:$mongoDbPassword@appfranceassossantedb.oq2bv.mongodb.net/?retryWrites=true&w=majority&appName=AppFranceAssosSanteDB"
    private val database = getMongoClient().getDatabase("AppFranceAssosSanteDB")

    private fun getMongoClient(): MongoClient {
        return MongoClients.create(CONNECTION_STRING)
    }

    fun saveUser(civilite:String, nom:String, prenom:String, email:String, mdp:String,handicap:String) {
        try {
            // Sélectionner la collection
            val collection: MongoCollection<Document> = database.getCollection("users")
            // Créer un document à insérer
            val newUser = Document("_id",email) // clé
                .append("civilite",civilite)
                .append("nom", nom)
                .append("prenom",prenom)
                .append("mdp",mdp)
                .append("handicap",handicap)

            // Insérer le document
            collection.insertOne(newUser)

            println("Document inséré avec succès.")
        } catch (e: Exception) {
            println("Erreur lors de l'insertion du document : ${e.message}")
        }
    }

    fun findUserByEmail(email: String): Utilisateur? {
        val collection: MongoCollection<Document> = database.getCollection("users")

        // Rechercher l'utilisateur par email dans la collection
        val document = collection.find(Document("email", email)).first()

        // Si un utilisateur est trouvé, le convertir en objet Utilisateur
        return if (document != null) {
            Utilisateur(
                civilite = document.getString("civilite"),
                nom = document.getString("nom"),
                prenom = document.getString("prenom"),
                email = document.getString("email"),
                mdp = document.getString("mdp"),
                handicap = document.getString("handicap")
            )
        } else {
            null // Si l'utilisateur n'est pas trouvé
        }
    }

    fun isEmailAlreadyUsed(email: String): Boolean {
        return try {
            // Accéder à la collection "users"
            val collection: MongoCollection<Document> = database.getCollection("users")

            // Rechercher un document avec l'email donné
            val document = collection.find(Document("email", email)).first()

            // Si un document est trouvé, l'email est déjà utilisé
            document != null
        } catch (e: Exception) {
            println("Erreur lors de la vérification de l'email : ${e.message}")
            false
        }
    }

//    // créer une clé composé
//    fun createUniqueComposedIndexDonation() {
//        val collection: MongoCollection<Document> = database.getCollection("donation")
//
//        // Créer un index composé unique sur les champs "emailUtilisateur" et "association"
//        collection.createIndex(
//            Indexes.compoundIndex(
//                Indexes.ascending("emailUtilisateur"),
//                Indexes.ascending("association"),
//            ),
//            com.mongodb.client.model.IndexOptions().unique(true) // Ajouter la contrainte d'unicité
//        )
//
//        println("Index composé unique créé avec succès.")//
//    }
//
//    fun saveNewDonation(montant: Double, date: Date, association : String, emailUtilisateur: String, frequence: String?, dateFin: Date?) {
//        try {
//            // Sélectionner la collection
//            val collection: MongoCollection<Document> = database.getCollection("donation")
//
////            val newDonation = Document("_id",email) // clé
////                .append("civilite",civilite)
////                .append("nom", nom)
////                .append("prenom",prenom)
////                .append("mdp",mdp)
////                .append("handicap",handicap)
//
//            // Insérer le document
//            collection.insertOne(newDonation)
//
//            println("Document inséré avec succès.")
//        } catch (e: Exception) {
//            println("Erreur lors de l'insertion du document : ${e.message}")
//        }
//    }


}
