package com.example.appfranceassossante.apiService

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

// Classe pour supprimer le don récurrent
class DeleteDonRTask(private val context: Context) {

    // Fonction qui effectue la suppression du don dans un thread d'arrière-plan
    suspend fun deleteDonInBG(emailUser: String, frequence: String, assos: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // URL de la requête sans les paramètres dans l'URL
                val url = URL("http://10.0.2.2:5000/deleteDonRecurrent")
                Log.d("DeleteDonRTask", "param : $emailUser,$frequence,$assos")

                // Ouverture de la connexion HTTP
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true // Indique qu'on va envoyer des données dans le corps

                // Création de l'objet JSON avec les données à envoyer
                val jsonBody = JSONObject()
                jsonBody.put("emailUser", emailUser)
                jsonBody.put("frequence", frequence)
                jsonBody.put("assos", assos)


                // Envoi des données dans le corps de la requête
                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                // Envoi de la requête et récupération du code de réponse
                val responseCode = connection.responseCode
                Log.d("DeleteDonRTask", "Code de réponse du serveur : $responseCode")

                // Vérification du code de réponse HTTP
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("DeleteDonRTask", "Don supprimé avec succès.")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Don supprimé", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    Log.e("DeleteDonRTask", "Erreur lors de la suppression du don. Code réponse: $responseCode")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur lors de la suppression du don", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            } catch (e: Exception) {
                // Log des erreurs de connexion ou d'exécution
                Log.e("DeleteDonRTask", "Erreur réseau lors de la suppression du don", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }

    // Fonction d'exécution qui appelle la méthode en arrière-plan
    fun execute(emailUser: String, frequence: String, assos: String, callback: (Boolean) -> Unit = {}) {
        Log.d("DeleteDonRTask", "Exécution de la tâche de suppression pour $emailUser, $frequence, $assos")
        CoroutineScope(Dispatchers.Main).launch {
            // Appel à la méthode de suppression
            val success = deleteDonInBG(emailUser, frequence, assos)
            Log.d("DeleteDonRTask", "Tâche terminée, succès: $success")
            callback(success)
        }
    }
}
