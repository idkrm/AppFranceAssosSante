import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.Assos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.appfranceassossante.models.User
import kotlinx.coroutines.launch

class CreateDonUTask(private val context: Context) {

    suspend fun createDonUInBG(user: User, assos: Assos): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/donations")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("userEmail", user.email)
                }

                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Utilisateur créé avec succès", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur lors de la création. Code: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            } catch (e: Exception) {
                Log.e("CreateUserTask", "Erreur réseau", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur de connexion: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }

//    fun execute(user: User) {
//        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
//            val success = createUserInBG(user)
//            Log.d(TAG, "Tâche terminée - Succès: $success")
//        }
//    }
}