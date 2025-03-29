package com.example.appfranceassossante.utilsAccessibilite.textSize

import android.content.Context
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import com.example.appfranceassossante.R
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

object TextSizeManager {
    private val registeredViews = mutableListOf<WeakReference<TextView>>()
    private const val PREF_NAME = "text_size_prefs"
    private const val PREF_SIZE_OFFSET = "size_offset"

    var sizeOffset: Float by Delegates.observable(0f) { _, _, newValue ->
        updateAllViews()
        saveSizeOffset()
    }

    fun init(context: Context) {
        loadSizeOffset(context)
    }

    private fun saveSizeOffset() {
        val prefs = MyApp.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(PREF_SIZE_OFFSET, sizeOffset).apply()
    }

    private fun loadSizeOffset(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sizeOffset = prefs.getFloat(PREF_SIZE_OFFSET, 0f)
    }

    fun registerTextView(textView: TextView) {
        if (textView is EditText) return

        if (textView.getTag(R.id.original_text_size) == null) {
            val originalSize = textView.textSize / textView.resources.displayMetrics.scaledDensity
            textView.setTag(R.id.original_text_size, originalSize)
        }

        registeredViews.add(WeakReference(textView))
        applySize(textView)
    }

    private fun updateAllViews() {
        registeredViews.removeAll { it.get() == null }
        registeredViews.forEach { it.get()?.let(TextSizeManager::applySize) }
    }

    private fun applySize(textView: TextView) {
        val originalSize = textView.getTag(R.id.original_text_size) as? Float ?: run {
            val size = textView.textSize / textView.resources.displayMetrics.scaledDensity
            textView.setTag(R.id.original_text_size, size)
            size
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalSize + sizeOffset)
    }
}