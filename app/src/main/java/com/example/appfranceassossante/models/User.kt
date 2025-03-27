package com.example.appfranceassossante.models

import androidx.lifecycle.MutableLiveData

data class User(
    val email: String,
    var civilite: String,
    var nom: String,
    var mdp: String,
    var prenom: String,
    var handicap: String,
    var admin: Assos?,
){
    fun getUserNom(): String{
        return nom
    }
    fun getUserPrenom(): String{
        return prenom
    }
}
