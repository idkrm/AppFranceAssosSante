package com.example.appfranceassossante.models

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

    private val _admin = MutableLiveData<String>()
    val admin : LiveData<String> get() = _admin

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

    fun setAdmin(value: String?) {
        _admin.value = value
    }

    fun collectUserData(): User {
        return User(
            civilite = _civilite.value ?: "",
            nom = _nom.value ?: "",
            prenom = _prenom.value ?: "",
            email = _mail.value ?: "",
            mdp = _mdp.value ?: "",
            handicap = _handicap.value ?: "",
            admin = _admin.value ?: ""
        )
    }

    fun reinitialiserDonnees() {
        _civilite.value = null
        _nom.value = null
        _prenom.value = null
        _mail.value = null
        _mdp.value = null
        _handicap.value = null
        _admin.value = null
    }

    fun updateUserData(user: User){
        setCivilite(user.civilite)
        setNom(user.nom)
        setPrenom(user.prenom)
        setMail(user.email)
        setMdp(user.mdp)
        setHandicap(user.handicap)
        setAdmin(user.admin)
    }

    /*
    fun getDatabase(): MongoDatabase {
        return mongoDBConnection.getDatabase()
    }

    suspend fun testConnection() {
        mongoDBConnection.testConnection()
    }
     */
}