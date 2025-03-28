package com.example.appfranceassossante.apiService

import android.content.Context
import android.util.Log
import com.example.appfranceassossante.models.Assos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetAssosTask(private val onResult: (List<Assos>) -> Unit) {

    suspend fun getAssosInBackground(): List<Assos> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/associations/assos")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                val associationsList = mutableListOf<Assos>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
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
            } catch (e: Exception) {
                Log.e("GetAssosTask", "Error fetching associations", e)
                emptyList()
            }
        }
    }

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            val associations = getAssosInBackground()
            onResult(associations)
        }
    }
}