package com.example.appfranceassossante.apiService

import android.util.Log
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.models.DonRecurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class GetDonRecUserTask {
    suspend fun getDonRecUserInBG(mail: String): List<DonRecurrent> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/donations/donsrec/user/$mail")
                Log.d("GetDonRecUserTask", "URL appelée: $url")

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                Log.d("GetDonRecUserTask", "Code réponse HTTP: ${connection.responseCode}")

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        connection.inputStream.bufferedReader().use { reader ->
                            val responseText = reader.readText()
                            Log.d("GetDonRecUserTask", "Réponse brute de l'API: $responseText")
                            val jsonResponse = JSONArray(responseText)

                            val donsRecList = mutableListOf<DonRecurrent>()

                            for (i in 0 until jsonResponse.length()) {
                                try{
                                    val jsonObject = jsonResponse.getJSONObject(i)
                                    val associationJson = jsonObject.optJSONObject("association")

                                    if (associationJson == null) {
                                        throw JSONException("L'association est manquante")
                                    }

                                    // On récupère le nom de l'association depuis l'objet JSON
                                    val associationName = associationJson.optString("nom", "Association inconnue")


                                    val donRec = DonRecurrent(
                                        emailUtilisateur = jsonObject.getString("utilisateurEmail"),
                                        montant = jsonObject.getDouble("montant"),
                                        date = Don.parseDate(jsonObject.getString("date")),
                                        paiement = jsonObject.getString("typePaiement"),
                                        frequence = jsonObject.getString("frequence"),
                                        dateFin = Don.parseDate(jsonObject.getString("dateFin")),
                                        association = associationName,

                                    )
                                    Log.i("GetDonRecUserTask", "Nombre de dons récurrents récupérés: ${donsRecList.size}")
                                    donsRecList.add(donRec)
                                    Log.i("GetDonRecUserTask", "Nombre de dons récurrents récupérés: ${donsRecList.size}")
                                } catch (e: JSONException) {
                                    Log.e("GetDonRecUserTask", "Don invalide à l'index $i", e)
                                }
                            }
                            donsRecList
                        }
                    }

                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Log.i("GetDonRecUserTask", "Aucun don trouvé pour l'utilisateur: $mail")
                        emptyList()
                    }

                    else -> {
                        Log.e("GetDonRecUserTask", "Erreur serveur: ${connection.responseCode}")
                        emptyList()
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("GetDonRecUserTask", "Timeout serveur", e)
                emptyList()
            } catch (e: JSONException) {
                Log.e("GetDonRecUserTask", "Erreur de parsing JSON", e)
                emptyList()
            } catch (e: Exception) {
                Log.e("GetDonRecUserTask", "Erreur réseau", e)
                emptyList()
            }
        }
    }
}