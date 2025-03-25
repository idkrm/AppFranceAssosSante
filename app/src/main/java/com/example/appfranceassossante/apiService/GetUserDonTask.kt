package com.example.appfranceassossante.apiService

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.Don
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetDonationsTask(private val context: Context) {

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getDonationsInBG(mail: String): List<Don>? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("http://10.0.2.2:5000/donations/user/$mail")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        showToast("Chargement des dons...")

                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        val jsonResponse = JSONArray(reader.readText())
                        reader.close()

                        val donationsList = mutableListOf<Don>()

                        for (i in 0 until jsonResponse.length()) {
                            val donObject = jsonResponse.getJSONObject(i)
                            val associationObj = donObject.optJSONObject("association")

                            val don = Don(
                                montant = donObject.optDouble("montant", 0.0),
                                association = associationObj?.optString("nom", "") ?: "",
                                date = donObject.optString("date", ""),
                                emailUtilisateur = donObject.optString("emailUtilisateur", ""),
                                paiement = donObject.optString("typePaiement", "")
                            )

                            donationsList.add(don)
                        }
                        return@withContext donationsList
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        showToast("Aucun don trouvé")
                        null
                    }
                    else -> {
                        Log.e("GetDonationsTask", "Erreur serveur: ${connection.responseCode}")
                        null
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("GetDonationsTask", "Timeout serveur", e)
                null
            } catch (e: JSONException) {
                Log.e("GetDonationsTask", "Erreur de parsing JSON", e)
                null
            } catch (e: Exception) {
                Log.e("GetDonationsTask", "Erreur réseau", e)
                null
            } finally {
                connection?.disconnect()
            }
        }
    }

    fun execute(mail: String, callback: (List<Don>?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val donations = getDonationsInBG(mail)
            callback(donations)
        }
    }
}
