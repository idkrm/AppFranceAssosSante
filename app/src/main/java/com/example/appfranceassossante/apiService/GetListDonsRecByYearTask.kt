package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.models.DonRecurrent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.Date

class GetListDonsRecByYearTask(private val year: String, private val assosID: String, private val onSuccess: (List<DonRecurrent>) -> Unit, private val onError: (String) -> Unit) {
    suspend fun getListDonsRecByYearInBG(): List<DonRecurrent> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/rec/details/$assosID/$year")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        val response = connection.inputStream.bufferedReader()
                            .use(BufferedReader::readText)
                        parseResponse(response)
                    }

                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Log.i(
                            "GetListDonsRecByYear",
                            "Aucun don trouvé pour l'association: $assosID et année: $year"
                        )
                        emptyList()
                    }

                    else -> {
                        val error = "Erreur serveur: ${connection.responseCode}"
                        Log.e("GetListDonsRecByYear", error)
                        throw Exception(error)
                    }
                }
            } catch (e: Exception) {
                Log.e("GetListDonsRecByYear", "Erreur réseau", e)
                throw e
            }
        }
    }

    private fun parseResponse(jsonResponse: String): List<DonRecurrent> {
        return try {
            val jsonObject = JSONObject(jsonResponse)

            val donsArray = jsonObject.getJSONArray("dons")
            val donsList = mutableListOf<DonRecurrent>()

            List(donsArray.length()) { i ->
                    val donJson = donsArray.getJSONObject(i)
                    DonRecurrent(
                        montant = donJson.getDouble("montant"),
                        date = Don.parseDate(donJson.getString("date")) ?: Date(),
                        dateFin = Don.parseDate(donJson.getString("dateFin")) ?: Date(),
                        emailUtilisateur = donJson.getString("utilisateurEmail"),
                        frequence = donJson.getString("frequence"),
                        paiement = donJson.getString("typePaiement"),
                        association = donJson.optString("association", "")
                    )
            }
        } catch (e: Exception) {
            Log.e("GetListDonsRecByYear", "Erreur de parsing", e)
            throw e
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val dons = getListDonsRecByYearInBG()
                onSuccess(dons)
            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue")
            }
        }
    }
}
