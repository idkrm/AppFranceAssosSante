package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.DonsAggregate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class GetListDonsRecByYearMonthTask(private val year: String, private val month: String, private val assosID: String, private val onSuccess: (DonsAggregate) -> Unit, private val onError: (String) -> Unit) {

    suspend fun getListDonsRecByYearMonthInBG(): DonsAggregate {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/rec/details-mensuel/$assosID/$year/$month")
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
                            "GetListDonsRecByYearMonth",
                            "Aucun don trouvé pour l'association: $assosID et année: $year et mois: $month"
                        )
                        DonsAggregate(0.0, 0)  // Retourne un aggregate vide
                    }

                    else -> {
                        val error = "Erreur serveur: ${connection.responseCode}"
                        Log.e("GetListDonsRecByYearMonth", error)
                        throw Exception(error)
                    }
                }
            } catch (e: Exception) {
                Log.e("GetListDonsRecByYearMonth", "Erreur réseau", e)
                throw e
            }
        }
    }

    private fun parseResponse(jsonResponse: String): DonsAggregate {
        return try {
            Log.d("GetListDonsRecByYearMonth", "Réponse JSON reçue: $jsonResponse")
            val jsonObject = JSONObject(jsonResponse)

            val dataObject  = jsonObject.getJSONObject("data")

            DonsAggregate(
                total = dataObject.getDouble("total"),
                count = dataObject.getInt("count")
            )
        } catch (e: Exception) {
            Log.e("GetListDonsRecByYearMonth", "Erreur de parsing", e)
            throw e
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val dons = getListDonsRecByYearMonthInBG()
                onSuccess(dons)
            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue")
            }
        }
    }
}
