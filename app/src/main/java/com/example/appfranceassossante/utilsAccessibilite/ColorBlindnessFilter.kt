package com.example.appfranceassossante.utilsAccessibilite

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.view.Window

object ColorBlindnessFilter {
    private var currentFilter: ColorMatrixColorFilter? = null

    private val protanopiaCorrectionMatrix = floatArrayOf(
        0.817f, 0.183f, 0.0f, 0.0f, 0.0f,
        0.333f, 0.667f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.125f, 0.875f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )

    private val deuteranopiaCorrectionMatrix = floatArrayOf(
        0.8f, 0.2f, 0.0f, 0.0f, 0.0f,
        0.258f, 0.742f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.142f, 0.858f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )

    private val tritanopiaCorrectionMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, -0.395f, 1.395f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )


    fun applyFilter(window: Window, mode: String) {
        val matrix = getMatrixForMode(mode)
        val filter = ColorMatrixColorFilter(ColorMatrix(matrix))
        currentFilter = filter

        val paint = Paint()
        paint.colorFilter = filter

        // Appliquer le filtre sur toute l'interface
        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }

    fun removeFilter(window: Window) {
        currentFilter = null
        window.decorView.setLayerType(View.LAYER_TYPE_NONE, null)
    }

    private fun getMatrixForMode(mode: String): FloatArray {
        return when (mode) {
            "protanopie" -> protanopiaCorrectionMatrix
            "deuteranopie" -> deuteranopiaCorrectionMatrix
            "tritanopie" -> tritanopiaCorrectionMatrix
            else -> floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        }
    }
}
