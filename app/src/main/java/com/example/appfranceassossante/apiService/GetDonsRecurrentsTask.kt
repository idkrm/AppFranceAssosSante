package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.DonsRecurrentAggregate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class GetDonsRecurrentsTask(
    private val year: String,
    private val month: String,
    private val assosID: String,
    private val onSuccess: (DonsRecurrentAggregate) -> Unit,
    private val onError: (String) -> Unit
) {

    suspend fun fetchDonsRecurrentInBG(): DonsRecurrentAggregate {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/recurrents/$assosID/$year/$month")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        val response =
                            connection.inputStream.bufferedReader().use(BufferedReader::readText)
                        parseResponse(response)
                    }

                    else -> {
                        val error = "Erreur serveur: ${connection.responseCode}"
                        Log.e("GetDonsRecurrentTask", error)
                        throw Exception(error)
                    }
                }
            } catch (e: Exception) {
                Log.e("GetDonsRecurrentTask", "Erreur réseau", e)
                throw e
            }
        }
    }

    private fun parseResponse(jsonResponse: String): DonsRecurrentAggregate {
        return try {
            Log.d("GetDonsRecurrentTask", "Réponse JSON reçue: $jsonResponse")
            val jsonObject = JSONObject(jsonResponse)
            val dataObject = jsonObject.getJSONObject("data")

            DonsRecurrentAggregate(
                totalMensuel = dataObject.getDouble("totalMensuel"),
                countMensuel = dataObject.getInt("countMensuel"),
                totalAnnuel = dataObject.getDouble("totalAnnuel"),
                countAnnuel = dataObject.getInt("countAnnuel")
            )
        } catch (e: Exception) {
            Log.e("GetDonsRecurrentTask", "Erreur de parsing", e)
            throw e
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val donsRecurrent = fetchDonsRecurrentInBG()
                onSuccess(donsRecurrent)
            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue")
            }
        }
    }
}