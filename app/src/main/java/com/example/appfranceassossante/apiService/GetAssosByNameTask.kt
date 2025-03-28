package com.example.appfranceassossante.apiService

import android.content.ContentValues.TAG
import android.util.Log
import com.example.appfranceassossante.models.Assos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetAssosByNameTask {
    suspend fun getAssosByNameInBG(nom: String): Assos? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/associations/assos/$nom")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        connection.inputStream.bufferedReader().use { reader ->
                            val jsonResponse = JSONObject(reader.readText())
                            Assos(
                                nom = jsonResponse.getString("nom"),
                                img = jsonResponse.optString("img"),
                                description = jsonResponse.optString("description"),
                                filtre = jsonResponse.optString("filtre"),
                                acronyme = jsonResponse.optString("civilite")
                            )
                        }
                    }

                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Log.e(TAG, "Association non trouvé")
                        null
                    }

                    else -> {
                        Log.e(TAG, "Erreur serveur: ${connection.responseCode}")
                        null
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout serveur", e)
                null
            } catch (e: JSONException) {
                Log.e(TAG, "Erreur de parsing JSON", e)
                null
            } catch (e: Exception) {
                Log.e(TAG, "Erreur réseau", e)
                null
            }
        }
    }
}