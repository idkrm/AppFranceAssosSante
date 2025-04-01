package com.example.appfranceassossante.apiService

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetListYearDonRecTask(private val assosID: String, private val onResult: (List<String>) -> Unit) {
    suspend fun getListYearDonRecInBG(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons_rec/annee/$assosID")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                }

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                        parseResponse(response)
                    }
                    else -> {
                        val errorMsg = "Erreur serveur: ${connection.responseCode}"
                        Log.e(TAG, errorMsg)
                        throw Exception(errorMsg)
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout de connexion", e)
                throw Exception("Timeout de connexion au serveur")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur réseau", e)
                throw Exception("Erreur de connexion: ${e.message}")
            }
        }
    }

    private fun parseResponse(response: String): List<String> {
        return try {
            JSONArray(response).let { jsonArray ->
                (0 until jsonArray.length()).map { jsonArray.getString(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur de parsing JSON", e)
            throw Exception("Format de réponse invalide")
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val listYears = getListYearDonRecInBG()
            onResult(listYears) // Retourne la liste des années au callback
        }
    }
}