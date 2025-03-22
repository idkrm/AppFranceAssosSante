package com.example.appfranceassossante.models

data class User(
    val email: String,
    var civilite: String,
    var nom: String,
    var mdp: String,
    var prenom: String,
    var handicap: String
)
