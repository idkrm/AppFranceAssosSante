package com.example.appfranceassossante.apiService

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GetAssosIDTask(private val assosName: String, private val onResult: (String?) -> Unit) {

    private suspend fun getAssosIdInBG(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedName = URLEncoder.encode(assosName, "UTF-8")
                Log.d("Les dons fragment", "nom assos: $encodedName")
                val url = URL("http://10.0.2.2:5000/associations/association/id/$encodedName")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val responseCode = connection.responseCode
                Log.d("GetAssosIDTask", "Response code: $responseCode for URL: $url")
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext null // Retourne null si l'association n'est pas trouvée
                }

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()

                val jsonObject = JSONObject(response)
                Log.d("GetAssosIDTask", "Réponse serveur pour $assosName: $response")
                return@withContext jsonObject.optString("id", null) // Récupère l'ID ou null si non trouvé
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val assosId = getAssosIdInBG()
            onResult(assosId) // Retourne l'ID de l'association
        }
    }
}