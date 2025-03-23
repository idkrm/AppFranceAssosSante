import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.appfranceassossante.models.User

class CreateUserTask {
    suspend fun createUser(user: User): String {
        return withContext(Dispatchers.IO) {  // Exécute en arrière-plan (thread IO)
            try {
                val url = URL("http://10.0.2.2:5000/users/register") // URL du serveur
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Créer le corps de la requête (JSON)
                val jsonBody = JSONObject().apply {
                    put("nom", user.nom)
                    put("prenom", user.prenom)
                    put("email", user.email)
                    put("mdp", user.mdp)
                    put("civilite", user.civilite)
                    put("handicap", user.handicap)
                }

                // Envoie la requête
                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                // Lire la réponse du serveur
                val responseCode = connection.responseCode
                return@withContext if (responseCode == HttpURLConnection.HTTP_CREATED) {  // 201 créé avec succès
                    "Utilisateur créé avec succès"
                } else {
                    "Erreur lors de la création de l'utilisateur. Code de réponse: $responseCode"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Erreur de connexion : ${e.message}"
            }
        }
    }
}
