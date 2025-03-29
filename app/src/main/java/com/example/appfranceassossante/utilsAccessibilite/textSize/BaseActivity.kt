package com.example.appfranceassossante.utilsAccessibilite.textSize

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.post {
            registerAllTextViews(window.decorView.findViewById(android.R.id.content))
        }
    }

    protected fun registerAllTextViews(view: View) {
        when (view) {
            is TextView -> TextSizeManager.registerTextView(view)
            is ViewGroup -> {
                if (view !is AdapterView<*>) {
                    for (i in 0 until view.childCount) {
                        registerAllTextViews(view.getChildAt(i))
                    }
                }
            }
        }
    }
}