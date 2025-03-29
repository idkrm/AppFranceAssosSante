package com.example.appfranceassossante.apiService

import android.companion.AssociationInfo
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.Don
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetDonByAssos(private val assosNom: String, private val onResult: (List<Don>) -> Unit) {

    suspend fun getTotalYearDonRecInBG(assosID: String): List<Don> {
        return withContext(Dispatchers.IO) { // Exécute le code en arrière-plan
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/$assosID") // Assurez-vous que `nom` existe dans `Assos`
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext emptyList()
                }

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("dons")

                val donList = mutableListOf<Don>()
                for (i in 0 until jsonArray.length()) {
                    val donJson = jsonArray.getJSONObject(i)
                    val don = Don(
                        emailUtilisateur = donJson.getString("emailUtilisateur"),
                        montant = donJson.getDouble("montant"),
                        date = Don.parseDate(jsonObject.getString("date")),
                        paiement = donJson.getString("typePaiement"),
                        association = donJson.optString("association", "") // Correction ici
                    )
                    donList.add(don)
                }
                donList
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    fun exec(assosID: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val donList = getTotalYearDonRecInBG(assosID)
            onResult(donList) // Met à jour l’UI avec la liste des dons
        }
    }

    fun execute() {
        GetAssosIDTask(assosNom) { assosId ->
            if (assosId != null) {
                exec(assosId)
            } else {
                onResult(emptyList()) // Si pas d'ID, retour liste vide
            }
        }.execute()
    }
}