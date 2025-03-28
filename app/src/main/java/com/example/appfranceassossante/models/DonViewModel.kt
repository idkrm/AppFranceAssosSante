package com.example.appfranceassossante.models

import android.support.v4.os.IResultReceiver._Parcel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appfranceassossante.apiService.GetAssosByNameTask
import kotlinx.coroutines.runBlocking
import java.util.Date

class DonViewModel : ViewModel() {
    private val _associationName = MutableLiveData<String>()  // Valeur par défaut
    val associationName: LiveData<String> get() = _associationName

    fun setAssociationName(name: String) {
        _associationName.value = name
    }

    fun getAssociationName(): String {
        return _associationName.value ?: "Association inconnue" // Valeur par défaut si la valeur est null
    }

    private val _montant = MutableLiveData(10) // Valeur par défaut 10
    val montant: LiveData<Int> get() = _montant

    fun setMontant(value: Int) {
        _montant.value = value
    }

    private val _selectedDate = MutableLiveData<Date?>()
    val selectedDate: LiveData<Date?> = _selectedDate

    fun setSelectedDate(date: Date?) {
        _selectedDate.value = date
    }


    private var paymentType: String? = null

    fun setPaymentType(type: String) {
        paymentType = type
    }

    fun getPaymentType(): String? {
        return paymentType
    }

    // Vérifie si le don est unique (aucune date de fin n'a été sélectionnée)
    fun isUnique(): Boolean {
        return _selectedDate.value == null
    }

    // Vérifie si le don récurrent est mensuel (si date de fin existe et qu'un flag mensuel est activé)
    private var isMensuelFlag: Boolean = true // Par défaut, on suppose mensuel

    fun setMensuel(isMensuel: Boolean) {
        isMensuelFlag = isMensuel
    }

    fun isMensuel(): Boolean {
        return isMensuelFlag
    }

    private val _utilisateurEmail = MutableLiveData<String?>() // email de l'utilisateur, peut être null
    val utilisateurEmail: LiveData<String?> get() = _utilisateurEmail

    fun setUtilisateurEmail(email: String?) {
        _utilisateurEmail.value = email
    }

    fun getUtilisateurEmail(): String? {
        return _utilisateurEmail.value
    }

    private val _utilisateurEmailRec = MutableLiveData<String>() // email de l'utilisateur, doit être non nul
    val utilisateurEmailRec: LiveData<String> get() = _utilisateurEmailRec

    // Setter pour email utilisateur récurrent, email non nullable
    fun setUtilisateurEmailRec(email: String) {
        _utilisateurEmailRec.value = email
    }

    // Cette méthode permet de récupérer un email utilisateur récurrent non nullable
    fun getUtilisateurEmailRec(): String {
        return _utilisateurEmailRec.value ?: "Email inconnu"  // Valeur par défaut si c'est null
    }


    fun collectDonUData(): Don {
        val montantDon = _montant.value ?: 10
        val associationName = _associationName.value ?:"Association des Malades Invalides et handicapés"
        val dateDon = Date()
        val utilisateurEmail = _utilisateurEmail.value
        val utilisateurEmailRec = getUtilisateurEmailRec()
        val typePaiement = paymentType ?: "Inconnu"

        // Récupération de l'association
        val association: Assos = runBlocking {
            GetAssosByNameTask().getAssosByNameInBG(associationName)
                ?: throw IllegalStateException("Impossible de récupérer l'association")
        }

        return Don(montantDon.toDouble(), association, dateDon, utilisateurEmail, typePaiement)

    }

    fun collectDonRData(): DonRecurrent{
        val montantDon = _montant.value ?: 10
        val associationName = _associationName.value ?:"Association des Malades Invalides et handicapés"
        val dateDon = Date()
        val utilisateurEmailRec = getUtilisateurEmailRec()
        val typePaiement = paymentType ?: "Inconnu"

        // Récupération de l'association
        val association: Assos = runBlocking {
            GetAssosByNameTask().getAssosByNameInBG(associationName)
                ?: throw IllegalStateException("Impossible de récupérer l'association")
        }

        val dateFin = _selectedDate.value ?: Date()
        val type : String
        if(isMensuel())
            type = "Mensuel"
        else
            type = "Annuel"
        return DonRecurrent(montantDon.toDouble(), dateDon, association, utilisateurEmailRec, typePaiement, type,dateFin)

    }

    fun reinitialiserDonnees() {
        _montant.value = null
        _selectedDate.value = null
        _associationName.value = null
        _utilisateurEmail.value = null
        _utilisateurEmailRec.value = null
    }
}
