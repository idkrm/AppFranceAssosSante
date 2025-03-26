package com.example.appfranceassossante.apiService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

class GetTotalYearDonRecTask(private val year: String, private val onResult: (Int) -> Unit) {
    suspend fun getTotalYearDonRecInBG(): Int {
        return try {
            val url = URL("http://10.0.2.2:5000/donations/dons/rec/total/$year")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")

            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readText()
            reader.close()

            val jsonObject = JSONObject(response)
            jsonObject.getInt("total_rec")
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val total = getTotalYearDonRecInBG()
            onResult(total) // Met à jour l’UI avec la somme des dons
        }
    }
}