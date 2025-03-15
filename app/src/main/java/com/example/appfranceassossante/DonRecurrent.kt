package com.example.appfranceassossante

import java.util.Date

class DonRecurrent(
    montant: Double,
    date: Date,
    association : String,
    emailUtilisateur: String,
    paiement: String,
    val frequence: String,
    val dateFin: Date
) : Don(montant,association, date, emailUtilisateur, paiement)  // Héritage de la classe Don