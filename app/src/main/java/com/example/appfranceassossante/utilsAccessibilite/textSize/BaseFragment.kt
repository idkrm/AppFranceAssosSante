package com.example.appfranceassossante.utilsAccessibilite.textSize

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

abstract class BaseFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attend que la vue soit complètement chargée
        view.post {
            registerAllTextViews(view)
        }
    }

    protected fun registerAllTextViews(view: View) {
        when (view) {
            is TextView -> TextSizeManager.registerTextView(view)
            is ViewGroup -> {
                if (view !is RecyclerView && view !is AdapterView<*>) {
                    for (i in 0 until view.childCount) {
                        registerAllTextViews(view.getChildAt(i))
                    }
                }
            }
        }
    }
}