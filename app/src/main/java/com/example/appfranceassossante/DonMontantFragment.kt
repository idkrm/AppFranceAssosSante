package com.example.appfranceassossante

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class DonMontantFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_montant, container, false)

        val suivant = view.findViewById<Button>(R.id.suivant)
        suivant.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonTypeFragment"
            transaction.replace(R.id.fragment_container, DonTypeFragment())
            transaction.addToBackStack(null) // met le fragment actuel en backstack (pour y retourner rapidement en faisant retour)
            transaction.commit()
        }

        val retour = view.findViewById<Button>(R.id.retour)
        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonFragment"
            transaction.replace(R.id.fragment_container, DonFragment())
            transaction.commit()
        }
        return view;
    }
}