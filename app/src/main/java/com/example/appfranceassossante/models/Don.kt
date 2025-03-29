package com.example.appfranceassossante.models

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

open class Don(
    val montant: Double,
    val association: String, //nom de l'assos
    val date: Date,
    val emailUtilisateur: String?,
    val paiement: String
){
    companion object {
        fun parseDate(dateString: String): Date {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                },
                SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            )

            formats.forEach { format ->
                try {
                    val parsedDate = format.parse(dateString)
                    if (parsedDate != null) {
                        return parsedDate
                    }
                } catch (e: Exception) {
                    // Continue to next format
                }
            }
            Log.w("Don", "Aucun format de date ne correspond, utilisation de la date actuelle")
            return Date()
        }
    }
}
