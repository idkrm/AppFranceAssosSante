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
import java.net.URL

class GetListYearDonRecTask(private val onResult: (List<String>) -> Unit) {
    suspend fun getListYearDonRecInBG(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons_rec/annee")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                val yearsList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    yearsList.add(jsonArray.getString(i))
                }
                yearsList
            } catch (e: Exception) {
                Log.e(TAG, "Timeout serveur", e)
                emptyList()
            }
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val listYears = getListYearDonRecInBG()
            onResult(listYears) // Retourne la liste des années au callback
        }
    }
}