import android.util.Log
import com.example.appfranceassossante.models.User
import android.content.ContentValues.TAG
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlinx.coroutines.launch

class CreateUserTask(private val context: Context) {

    suspend fun createUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/users/register")
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

                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonBody.toString().toByteArray())
                    outputStream.flush()
                }

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_CREATED -> {
                        showToast("Utilisateur créé avec succès")
                        true
                    }
                    else -> {
                        showToast("Erreur lors de la création. Code: ${connection.responseCode}")
                        false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur réseau", e)
                showToast("Erreur de connexion: ${e.message}")
                false
            }
        }
    }

    suspend fun findUserByMail(email: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/users/findUser")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                JSONObject().apply {
                    put("email", email)
                }.toString().toByteArray().let { jsonData ->
                    connection.outputStream.use { it.write(jsonData) }
                }

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        connection.inputStream.bufferedReader().use { reader ->
                            val jsonResponse = JSONObject(reader.readText())
                            User(
                                nom = jsonResponse.optString("nom"),
                                prenom = jsonResponse.optString("prenom"),
                                email = jsonResponse.getString("email"),
                                mdp = jsonResponse.getString("mdp"),
                                civilite = jsonResponse.optString("civilite"),
                                handicap = jsonResponse.optString("handicap"),
                                role = jsonResponse.optString("role")
                            )
                        }
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> null
                    else -> {
                        Log.e(TAG, "Erreur serveur: ${connection.responseCode}")
                        null
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout serveur", e)
                null
            } catch (e: JSONException) {
                Log.e(TAG, "Erreur de parsing JSON", e)
                null
            } catch (e: Exception) {
                Log.e(TAG, "Erreur réseau", e)
                null
            }
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun execute(user: User) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val success = createUser(user)
            Log.d(TAG, "Tâche terminée - Succès: $success")
        }
    }
}