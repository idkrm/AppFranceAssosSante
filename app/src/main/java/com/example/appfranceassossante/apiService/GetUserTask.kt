import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetUserTask(private val context: Context) {

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


    suspend fun getUserInBG(email: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/users/user/$email")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        showToast("Connexion...")
                        connection.inputStream.bufferedReader().use { reader ->
                            val jsonResponse = JSONObject(reader.readText())
                            User(
                                nom = jsonResponse.optString("nom"),
                                prenom = jsonResponse.optString("prenom"),
                                email = jsonResponse.getString("email"),
                                mdp = jsonResponse.getString("mdp"),
                                civilite = jsonResponse.optString("civilite"),
                                handicap = jsonResponse.optString("handicap"),
                                admin = jsonResponse.optJSONObject("admin")?.let {
                                    Assos(
                                        nom = it.optString("nom", ""),
                                        img = it.optString("img", ""),
                                        description = it.optString("description", ""),
                                        filtre = it.optString("filtre", ""),
                                        acronyme = it.optString("acronyme", "")
                                    )
                                }
                            )
                        }
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        showToast("Utilisateur non trouvé")
                        null
                    }
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

    /*
    fun execute(email: String, callback: (User?) -> Unit) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val userData = getUserInBG(email)
            callback(userData)
        }
    }
     */
    fun execute(email: String) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val userData = getUserInBG(email)
            Log.d(TAG, "Recherche terminée")
        }
    }
}