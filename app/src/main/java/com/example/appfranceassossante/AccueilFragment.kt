package com.example.appfranceassossante

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class AccueilFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_accueil, container, false)

        val btn_don = view.findViewById<Button>(R.id.btn_don)
        val btn_assos = view.findViewById<Button>(R.id.btn_assos)

        return view
    }

}
