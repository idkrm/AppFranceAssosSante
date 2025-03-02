package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.appfranceassossante.R

class DonTypeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_type, container, false)

        val suivant = view.findViewById<Button>(R.id.suivant)
        suivant.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonPaiementFragment"
            transaction.replace(R.id.fragment_container, DonPaiementFragment())
            transaction.addToBackStack(null) // met le fragment actuel en backstack (pour y retourner rapidement en faisant retour)
            transaction.commit()
        }

        val retour = view.findViewById<Button>(R.id.retour)
        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonMontantFragment"
            transaction.replace(R.id.fragment_container, DonMontantFragment())
            transaction.commit()
        }
        return view;
    }
}