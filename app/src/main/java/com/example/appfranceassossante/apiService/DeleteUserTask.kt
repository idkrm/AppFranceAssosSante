import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class DeleteUserTask(private val context: Context) {

    suspend fun deleteUserInBG(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                val url = URL("http://10.0.2.2:5000/users/delete")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("email", email)
                }
                Log.d("DeleteUserTask", "JSON envoyé: $jsonBody") //

                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                val responseCode = connection.responseCode
                val responseMessage = connection.responseMessage//
                Log.d("DeleteUserTask", "Code: $responseCode, Message: $responseMessage") //
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Utilisateur supprimé avec succès", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur lors de la suppression. Code: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            } catch (e: Exception) {
                Log.e("DeleteUserTask", "Erreur réseau", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur de connexion: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }

    fun execute(email: String) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val success = deleteUserInBG(email)
            Log.d("DeleteUserTask", "Tâche terminée - Succès: $success")
        }
    }
}