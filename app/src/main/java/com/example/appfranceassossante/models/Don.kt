package com.example.appfranceassossante.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class Don(
    val montant: Double,
    val association: Assos,
    val date: Date,
    val emailUtilisateur: String?,
    val paiement: String
){
    companion object {
        fun parseDate(dateString: String): Date {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE),
                SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            )

            for (format in formats) {
                try {
                    return format.parse(dateString) ?: Date()
                } catch (e: Exception) {
                    continue
                }
            }
            return Date()
        }
    }
}
