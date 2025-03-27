package com.example.appfranceassossante.models

import android.support.v4.os.IResultReceiver._Parcel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class DonViewModel : ViewModel() {
    private val _associationName = MutableLiveData<String>()
    val associationName: LiveData<String> get() = _associationName

    fun setAssociationName(name: String) {
        _associationName.value = name
    }

    fun getAssociationName(): String {
        return _associationName.value ?: "Association inconnue"
    }

    private val _montant = MutableLiveData(10) // Valeur par défaut 10
    val montant: LiveData<Int> get() = _montant

    fun setMontant(value: Int) {
        _montant.value = value
    }

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    private var paymentType: String? = null

    fun setPaymentType(type: String) {
        paymentType = type
    }

    fun getPaymentType(): String? {
        return paymentType
    }
}
