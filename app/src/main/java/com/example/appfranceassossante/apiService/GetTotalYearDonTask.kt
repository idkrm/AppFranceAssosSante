package com.example.appfranceassossante.apiService

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

class GetTotalYearDonTask(private val year: String, private val assosID: String, private val onResult: (Double) -> Unit) {
    suspend fun getTotalYearDonInBG(): Double {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/total/$assosID/$year")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()

                val jsonObject = JSONObject(response)
                Log.d("GetTotalYearDonTask", "Réponse serveur pour $year: $response")
                val total = jsonObject.getDouble("total")

                Log.d(TAG, "Total reçu pour $year: $total")
                total
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val total = getTotalYearDonInBG()
            onResult(total) // Met à jour l’UI avec la somme des dons
        }
    }
}