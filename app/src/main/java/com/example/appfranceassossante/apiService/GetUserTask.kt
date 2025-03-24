import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GetUserTask(private val context: Context) {

    suspend fun getUserInBG(email: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/users/user/$email")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    JSONObject(response)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("GetUserTask", "Error fetching user", e)
                null
            }
        }
    }

    fun execute(email: String, callback: (JSONObject?) -> Unit) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            val userData = getUserInBG(email)
            callback(userData)
        }
    }
}