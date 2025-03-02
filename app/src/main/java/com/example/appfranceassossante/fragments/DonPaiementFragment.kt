package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.appfranceassossante.R

class DonPaiementFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_paiement, container, false)

        val payer = view.findViewById<Button>(R.id.payer)

        val retour = view.findViewById<Button>(R.id.retour)
        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonTypeFragment"
            transaction.replace(R.id.fragment_container, DonTypeFragment())
            transaction.commit()
        }
        return view;
    }
}