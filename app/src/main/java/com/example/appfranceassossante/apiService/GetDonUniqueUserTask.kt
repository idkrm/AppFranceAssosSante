package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.Don
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetDonUniqueUserTask {
     suspend fun getDonUniqueUserInBG(mail: String): List<Don> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/dons/user/$mail")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                            connection.inputStream.bufferedReader().use { reader ->
                                val responseText = reader.readText()
                                Log.i("GetDonUniqueUserTask", "Réponse brute de l'API: $responseText")

                                val jsonResponse = JSONArray(responseText)
                                val donsList = mutableListOf<Don>()

                                for (i in 0 until jsonResponse.length()) {
                                    try{
                                        val jsonObject = jsonResponse.getJSONObject(i)
                                        val associationJson = jsonObject.optJSONObject("association")
                                        if (associationJson == null) {
                                            throw JSONException("L'association est manquante")
                                        }

                                        // On récupère le nom de l'association depuis l'objet JSON
                                        val associationName = associationJson.optString("nom", "Association inconnue")

                                        Log.i("GetDonUniqueUserTask", "JSON reçu pour l'index $i: $jsonObject")

                                        val don = Don(
                                            emailUtilisateur = jsonObject.getString("emailUtilisateur"),
                                            montant = jsonObject.getDouble("montant"),
                                            association = associationName,
                                            date = Don.parseDate(jsonObject.getString("date")),
                                            emailUtilisateur = jsonObject.getString("emailUtilisateur"),
                                            paiement = jsonObject.getString("typePaiement"),
                                            association = associationName,
                                        )
                                        donsList.add(don)
                                        Log.i("GetDonUniqueUserTask", "Don ajouté: $don")
                                        Log.i("GetDonUniqueUserTask", "Nombre de dons récupérés: ${donsList.size}")
                                        donsList.add(don)
                                    } catch (e: JSONException) {
                                        Log.e("GetDonUniqueUserTask", "Don invalide à l'index $i", e)
                                    }
                                }
                                donsList
                            }
                    }

                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Log.i("GetDonUniqueUserTask", "Aucun don trouvé pour l'utilisateur: $mail")
                        emptyList()
                    }

                    else -> {
                        Log.e("GetDonUniqueUserTask", "Erreur serveur: ${connection.responseCode}")
                        emptyList()
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("GetDonUniqueUserTask", "Timeout serveur", e)
                emptyList()
            } catch (e: JSONException) {
                Log.e("GetDonUniqueUserTask", "Erreur de parsing JSON", e)
                emptyList()
            } catch (e: Exception) {
                Log.e("GetDonUniqueUserTask", "Erreur réseau", e)
                emptyList()
            }
        }
    }
}