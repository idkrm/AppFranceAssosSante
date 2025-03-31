package com.example.appfranceassossante.apiService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetMonthDonTask (private val year: String, private val assosID: String, private val onResult: (Map<String, Int>) -> Unit){

        suspend fun getMonthDonInBG(): Map<String, Int> {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL("http://10.0.2.2:5000/donations/dons/rec/mois/$assosID/$year")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Content-Type", "application/json")

                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    reader.close()

                    val jsonObject = JSONObject(response)
                    val donsParMois = mutableMapOf<String, Int>()

                    val mois = listOf("Jan", "Fev", "Mar", "Avr", "Mai", "Juin", "Juil", "Aout", "Sep", "Oct", "Nov", "Dec")

                    for (m in mois) {
                        donsParMois[m] = jsonObject.optInt(m, 0)
                    }

                    donsParMois
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyMap()
                }
            }
        }

        fun execute() {
            CoroutineScope(Dispatchers.Main).launch {
                val monthtotal = getMonthDonInBG()
                onResult(monthtotal) // Met à jour l’UI avec la somme des dons par mois
            }
        }
    }