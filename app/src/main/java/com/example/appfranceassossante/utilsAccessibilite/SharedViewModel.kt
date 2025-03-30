package com.example.appfranceassossante.utilsAccessibilite

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val _isSpeechEnabled = MutableLiveData<Boolean>()
    val isSpeechEnabled: LiveData<Boolean> = _isSpeechEnabled

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val prefs = getApplication<Application>().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        _isSpeechEnabled.value = prefs.getBoolean("tts_enabled", false)
    }

    fun enableSpeech(enable: Boolean) {
        _isSpeechEnabled.value = enable
        saveTtsState(enable)
    }

    private fun saveTtsState(enabled: Boolean) {
        getApplication<Application>().getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("tts_enabled", enabled)
            .apply()
    }
}