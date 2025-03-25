import android.util.Log
import com.example.appfranceassossante.models.User
import android.content.ContentValues.TAG
import android.content.Context
import android.widget.Toast
import com.example.appfranceassossante.models.Assos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlinx.coroutines.launch

class CreateUserTask(private val context: Context) {

    suspend fun createUserInBG(user: User): Boolean {
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
                    user.admin?.let {
                        put("admin", JSONObject().apply {
                            put("nom", it.getAssosName())
                            put("logo", it.getAssosLogo())
                        })
                    }
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

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun execute(user: User) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val success = createUserInBG(user)
            Log.d(TAG, "Tâche terminée - Succès: $success")
        }
    }
}