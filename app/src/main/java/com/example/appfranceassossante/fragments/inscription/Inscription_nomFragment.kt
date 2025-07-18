package com.example.appfranceassossante.fragments.inscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.UserViewModel


class Inscription_nomFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Initialiser le ViewModel
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_inscription_nom, container, false)

        val nom = view.findViewById<EditText>(R.id.nom)

        /*
        // Restaurer le nom si déjà saisi
        userViewModel.nom.observe(viewLifecycleOwner) {
            nom.setText(it)
        }
         */

        val btnsuivant = view.findViewById<Button>(R.id.suivant)
        btnsuivant.setOnClickListener {
            val nomSansEspace = nom.text.toString().trim()

            if (nomSansEspace.isEmpty())
                nom.error = getString(R.string.error_message_nom)
            else {
                userViewModel.setNom(nomSansEspace) // Enregistre le nom

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                // remplace le fragment actuel par le fragment qui suit ("Inscription_prenomFragment")
                transaction.replace(R.id.fragment_container, Inscription_prenomFragment())
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