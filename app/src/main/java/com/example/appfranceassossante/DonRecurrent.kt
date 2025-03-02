package com.example.appfranceassossante

import java.util.Date

class DonRecurrent(
    montant: Double,
    date: Date,
    association : String,
    emailUtilisateur: String,
    val frequence: String,
    val dateFin: Date
) : Don(montant,association, date, emailUtilisateur)  // Héritage de la classe Don