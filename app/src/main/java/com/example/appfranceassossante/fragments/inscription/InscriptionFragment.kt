package com.example.appfranceassossante.fragments.inscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.UserViewModel

class InscriptionFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        userViewModel.reinitialiserDonnees()

        val view = inflater.inflate(R.layout.fragment_inscription, container, false)

        val civilites = view.findViewById<RadioGroup>(R.id.civilite)

        var choix = ""

        civilites.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) { // Vérifie si un bouton est sélectionné
                val radioButton = view.findViewById<RadioButton>(checkedId)
                choix = radioButton.text.toString()
            }
        }

        val btnsuivant = view.findViewById<Button>(R.id.suivant)
        btnsuivant.setOnClickListener{
            if (civilites.checkedRadioButtonId != -1){
                userViewModel.setCivilite(choix)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                // remplace le fragment actuel par le fragment qui suit ("Inscription_nomFragment")
                transaction.replace(R.id.fragment_container, Inscription_nomFragment())
                transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
                transaction.commit()
            }
            else
                Toast.makeText(context,  getString(R.string.error_message_civ), Toast.LENGTH_SHORT).show()
        }

        val btnconnection = view.findViewById<Button>(R.id.connnection)
        btnconnection.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
            transaction.replace(R.id.fragment_container, SeConnecterFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        // Inflate the layout for this fragment
        return view
    }


}