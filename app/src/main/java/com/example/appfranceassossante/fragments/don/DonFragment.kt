package com.example.appfranceassossante.fragments.don

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DonFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate la vue avec le bon fragment
        val view = inflater.inflate(R.layout.fragment_don, container, false)

        // recup le bouton suivant
        val btn = view.findViewById<Button>(R.id.suivant)
        btn.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("DonMontantFragment")
            transaction.replace(R.id.fragment_container, DonMontantFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val assos = view.findViewById<TextView>(R.id.decouvrir)
        assos.setOnClickListener{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_assoc // selectionne "assos"
        }
        return view;
    }
}