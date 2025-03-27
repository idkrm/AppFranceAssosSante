package com.example.appfranceassossante.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class Don(
    val montant: Double,
    val association: String,
    val date: Date,
    val emailUtilisateur: String,
    val paiement: String
){
    companion object {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        fun parseDate(dateString: String): Date {
            return dateFormat.parse(dateString) ?: Date()
        }
    }
}
