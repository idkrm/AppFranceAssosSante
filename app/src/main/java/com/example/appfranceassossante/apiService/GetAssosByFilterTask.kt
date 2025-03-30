package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.Assos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetAssosByFilterTask {
    suspend fun getAssosByFilterInBG(filtre: String): List<Assos> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/associations/assos/filtre/$filtre")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        connection.inputStream.bufferedReader().use { reader ->
                            val jsonResponse = JSONArray(reader.readText())
                            val associationsList = mutableListOf<Assos>()

                            for (i in 0 until jsonResponse.length()) {
                                val jsonObject = jsonResponse.getJSONObject(i)
                                val association = Assos(
                                    acronyme = jsonObject.optString("acronyme", ""),
                                    filtre = jsonObject.optString("filtre", ""),
                                    description = jsonObject.optString("description", ""),
                                    img = jsonObject.optString("img", ""),
                                    nom = jsonObject.getString("nom")
                                )
                                associationsList.add(association)
                            }
                            associationsList
                        }
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Log.e("GetAssosByFilterTask", "Aucune association trouvée pour le filtre: $filtre")
                        emptyList()
                    }

                    else -> {
                        Log.e("GetAssosByFilterTask", "Erreur serveur: ${connection.responseCode}")
                        emptyList()
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("GetAssosByFilterTask", "Timeout serveur", e)
                emptyList()
            } catch (e: JSONException) {
                Log.e("GetAssosByFilterTask", "Erreur de parsing JSON", e)
                emptyList()
            } catch (e: Exception) {
                Log.e("GetAssosByFilterTask", "Erreur réseau", e)
                emptyList()
            }
        }
    }
}