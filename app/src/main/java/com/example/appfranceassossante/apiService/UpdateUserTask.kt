package com.example.appfranceassossante.apiService

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.appfranceassossante.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdateUserTask(private val context: Context) {

    suspend fun updateUserInBG(currentEmail: String, updatedUser: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:5000/users/update/$currentEmail")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                JSONObject().apply {
                    put("nom", updatedUser.nom)
                    put("prenom", updatedUser.prenom)
                    put("email", updatedUser.email)
                    put("mdp", updatedUser.mdp)
                    put("civilite", updatedUser.civilite)
                    put("handicap", updatedUser.handicap)
                }.let { json ->
                    connection.outputStream.use { it.write(json.toString().toByteArray()) }
                }

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("UpdateUser", "Réponse: $response")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Mise à jour réussie", Toast.LENGTH_LONG).show()
                        }
                        true
                    }
                    else -> {
                        val error = connection.errorStream?.bufferedReader()?.use { it.readText() }
                        Log.e("UpdateUser", "Erreur ${connection.responseCode}: $error")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Échec de la mise à jour", Toast.LENGTH_LONG).show()
                        }
                        false
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateUser", "Erreur réseau", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }

    fun execute(currentEmail: String, updatedUser: User, callback: (Boolean) -> Unit = {}) {
        CoroutineScope(Dispatchers.Main).launch {
            val success = updateUserInBG(currentEmail, updatedUser)
            callback(success)
        }
    }

}