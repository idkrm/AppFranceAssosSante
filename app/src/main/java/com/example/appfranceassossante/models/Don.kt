package com.example.appfranceassossante.models

import java.util.Date

open class Don(
    val montant: Double,
    val association : String,
    val date: Date,
    val emailUtilisateur: String,
    val paiement: String
)
