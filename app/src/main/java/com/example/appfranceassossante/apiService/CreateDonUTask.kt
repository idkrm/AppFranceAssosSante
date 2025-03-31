import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.Don
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.appfranceassossante.models.User
import kotlinx.coroutines.launch

class CreateDonUTask(private val context: Context) {

    suspend fun createDonUInBG(don : Don): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/donations")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val paiement : String
                if(don.paiement.equals("Credit card")||don.paiement.equals("Carte bancaire")||don.paiement.equals("银行卡"))
                    paiement = "CB"
                else if(don.paiement.equals("GooglePay"))
                    paiement = "GooglePay"
                else if(don.paiement.equals("ApplePay"))
                    paiement="ApplePay"
                else
                    paiement="PayPal"

                // Créer le corps de la requête (JSON)
                val jsonBody = JSONObject().apply {
                    put("montant", don.montant)
                    put("date", don.date)
                    put("utilisateurEmail", don.emailUtilisateur)
                    put("association", don.association)
                    put("typePaiement", paiement)
                }
                Log.d("DON_HTTP", "Envoi des données au serveur ")//

                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                val responseCode = connection.responseCode
                val response = connection.responseMessage
                Log.d("DON_HTTP", "Réponse serveur : $response")

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    withContext(Dispatchers.Main) {
                        Log.d("DON_HTTP", "Don créé avec succès")
                    }
                    true
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d("DON_HTTP", "Erreur lors de la création du don")
                    }
                    false
                }
            } catch (e: Exception) {
                Log.e("CreateDonUTask", "Erreur réseau", e)
                withContext(Dispatchers.Main) {
                    Log.d("DON_HTTP", "Erreur de connexion: ${e.message}")
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