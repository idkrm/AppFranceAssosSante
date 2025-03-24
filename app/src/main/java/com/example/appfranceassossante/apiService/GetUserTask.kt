import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GetUserTask {
    suspend fun getUserByEmail(email: String): String {
        return withContext(Dispatchers.IO) {  // Exécute en arrière-plan (thread IO)
            try {
                val url = URL("http://10.0.2.2:5000/users/user?email=$email") // URL du serveur avec l'email en paramètre
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                // Lire la réponse du serveur
                val responseCode = connection.responseCode
                return@withContext if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    response
                } else {
                    "Erreur lors de la récupération de l'utilisateur. Code de réponse: $responseCode"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Erreur de connexion : ${e.message}"
            }
        }
    }
}