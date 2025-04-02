import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.models.DonRecurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.appfranceassossante.models.User
import kotlinx.coroutines.launch

class CreateDonRTask(private val context: Context) {

    suspend fun createDonRInBG(don : DonRecurrent): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/recurring-donations")
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
                    put("dateFin",don.dateFin)
                    put("frequence",don.frequence)
                }

                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toString().toByteArray())
                outputStream.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "Don créé avec succès", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur lors de la création. Code: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            } catch (e: Exception) {
                Log.e("CreateDonUTask", "Erreur réseau", e)
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