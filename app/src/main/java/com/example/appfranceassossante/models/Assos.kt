package com.example.appfranceassossante.models

class Assos(
    private val nom: String,
    private val logo: Int,
) {
    fun getAssosName(): String{
        return nom
    }
    fun getAssosLogo(): Int{
        return logo
    }
}