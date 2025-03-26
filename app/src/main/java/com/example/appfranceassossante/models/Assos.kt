package com.example.appfranceassossante.models

class Assos(
    private val nom: String,
    private val img: String,
    private val description: String,
    private val acronyme: String,
    private val filtre: String,
) {
    fun getAssosName(): String{
        return nom
    }
    fun getImg(): String{
        return img
    }
    fun getAcronyme(): String{
        return acronyme
    }
    fun getFiltre(): String{
        return filtre
    }
    fun getDescription(): String{
        return description
    }

}