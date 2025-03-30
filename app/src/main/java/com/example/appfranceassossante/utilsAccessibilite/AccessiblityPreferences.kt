package com.example.appfranceassossante.utilsAccessibilite

import android.content.Context

object AccessibilityPreferences {
    private const val PREFS_NAME = "accessibility_prefs"
    private const val KEY_DALTONISM_ENABLED = "daltonism_enabled"
    private const val KEY_DALTONISM_TYPE = "daltonism_type"
    private const val KEY_TEXT_SIZE = "text_size"

    fun saveSpeechEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            .putBoolean("tts_enabled", enabled)
            .apply()
    }

    fun getSpeechEnabled(context: Context): Boolean {
        return context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getBoolean("tts_enabled", false)
    }

    fun saveDaltonismEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_DALTONISM_ENABLED, enabled)
            .apply()
    }

    fun getDaltonismEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DALTONISM_ENABLED, false)
    }

    fun saveDaltonismType(context: Context, type: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_DALTONISM_TYPE, type)
            .apply()
    }

    fun getDaltonismType(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DALTONISM_TYPE, null)
    }

    fun saveTextSize(context: Context, size: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putFloat(KEY_TEXT_SIZE, size)
            .apply()
    }

    fun getTextSize(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_TEXT_SIZE, 0f)
    }
}