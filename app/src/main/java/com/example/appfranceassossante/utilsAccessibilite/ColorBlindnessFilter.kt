package com.example.appfranceassossante.utilsAccessibilite

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.view.Window

object ColorBlindnessFilter {
    private var currentFilter: ColorMatrixColorFilter? = null

    private val protanopiaMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.183f, 0.817f, 0.0f, 0.0f, 0.0f,
        -0.106f, 0.229f, 0.877f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )

    private val deuteranopiaMatrix = floatArrayOf(
        0.290f, 0.710f, 0.0f, 0.0f, 0.0f,
        0.170f, 0.830f, 0.0f, 0.0f, 0.0f,
        -0.007f, 0.177f, 0.823f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )

    private val tritanopiaMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        -0.395f, 0.801f, 0.594f, 0.0f, 0.0f,
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
            "protanopie" -> protanopiaMatrix
            "deuteranopie" -> deuteranopiaMatrix
            "tritanopie" -> tritanopiaMatrix
            else -> floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        }
    }
}
