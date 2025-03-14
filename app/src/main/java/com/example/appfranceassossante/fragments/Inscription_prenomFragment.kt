package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.UserViewModel

class Inscription_prenomFragment : Fragment(){

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_inscription_prenom, container, false)
        val prenom = view.findViewById<EditText>(R.id.prenom)
        val btnsuivant = view.findViewById<Button>(R.id.suivant)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        btnsuivant.setOnClickListener{
            val prenomSansEspace = prenom.text.toString().trim()

            if(prenomSansEspace.isEmpty())
                prenom.error = "Veuillez entrer votre prénom"
            else {
                userViewModel.setPrenom(prenomSansEspace) // Enregistre le prénom
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                // remplace le fragment actuel par le fragment qui suit ("Inscription_adrmailFragment")
                transaction.replace(R.id.fragment_container, Inscription_adrmailFragment())
                transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
                transaction.commit()
            }
        }

        val btnretour = view.findViewById<Button>(R.id.retour)
        btnretour.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack() // retire le fragment actuel
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