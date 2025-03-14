package com.example.appfranceassossante

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val _civilite = MutableLiveData<String>()
    val civilite: LiveData<String> get() = _civilite

    private val _nom = MutableLiveData<String>()
    val nom: LiveData<String> get() = _nom

    private val _prenom = MutableLiveData<String>()
    val prenom: LiveData<String> get() = _prenom

    private val _mail = MutableLiveData<String>()
    val mail: LiveData<String> get() = _mail

    private val _mdp = MutableLiveData<String>()
    val mdp: LiveData<String> get() = _mdp

    private val _handicap = MutableLiveData<String>()
    val handicap : LiveData<String> get() = _handicap

    fun setCivilite(value: String) {
        _civilite.value = value
    }

    fun setNom(value: String) {
        _nom.value = value
    }

    fun setPrenom(value: String) {
        _prenom.value = value
    }

    fun setMail(value: String) {
        _mail.value = value
    }

    fun setMdp(value: String) {
        _mdp.value = value
    }

    fun setHandicap(value: String) {
        _handicap.value = value
    }

    fun collectUserData(): Map<String, String?> {
        return mapOf(
            "civilite" to _civilite.value,
            "nom" to _nom.value,
            "prenom" to _prenom.value,
            "email" to _mail.value,
            "mot de passe" to _mdp.value,
            "handicap" to _handicap.value,
        )
    }
}