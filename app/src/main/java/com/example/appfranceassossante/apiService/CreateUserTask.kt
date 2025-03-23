import android.os.AsyncTask
import android.util.Log
import com.example.appfranceassossante.models.User
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class CreateUserTask : AsyncTask<User, Void, String>() {

    override fun doInBackground(vararg params: User?): String {
        val user = params[0]

        return try {
            val url = URL("http://10.0.2.2:5000/users/register") // url du serveur pour créer un nouvel utilisateur
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Créer le corps de la requête (JSON)
            val jsonBody = JSONObject().apply {
                put("nom", user?.nom)
                put("prenom", user?.prenom)
                put("email", user?.email)
                put("password", user?.mdp)
                put("civilite", user?.civilite)
                put("handicap", user?.handicap)
            }

            // Envoie la requête
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(jsonBody.toString().toByteArray())
            outputStream.flush()

            // Lire la réponse du serveur
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_CREATED) { // si une nouvelle ressources a été créer
                // Si la requête réussit, retourne une réponse de succès
                "!!!Utilisateur créé avec succès"
            } else {
                // Si la requête échoue, retourne l'erreur
                "!!!Erreur lors de la création de l'utilisateur. Code de réponse: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "!!!Erreur de connexion : ${e.message}"
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        // Traite la réponse ici, par exemple, affiche le message de succès ou d'erreur
        Log.d("CreateUserTask", result ?: "Erreur inconnue")
    }
}

