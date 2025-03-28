package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.appfranceassossante.utilsTextSize.BaseFragment
import com.example.appfranceassossante.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccueilFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_accueil, container, false)

        val btn_don = view.findViewById<Button>(R.id.btn_don)
        btn_don.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_don // selectionne "don" dans le menu
        }

        val btn_assos = view.findViewById<Button>(R.id.btn_assos)
        btn_assos.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_assoc // selectionne "assos"
        }

        return view
    }

}
