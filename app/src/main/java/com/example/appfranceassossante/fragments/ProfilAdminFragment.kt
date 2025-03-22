package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel

class ProfilAdminFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil_admin, container, false)

        val civ = view.findViewById<TextView>(R.id.civilitepersonne)
        civ.text = userViewModel.civilite.toString()

        val nom = view.findViewById<TextView>(R.id.nompersonne)
        nom.text = userViewModel.nom.toString()

        val prenom = view.findViewById<TextView>(R.id.prenompersonne)
        prenom.text = userViewModel.prenom.toString()

        val mail = view.findViewById<TextView>(R.id.mailpersonne)
        mail.text = userViewModel.mail.toString()

        val btnlangue = view.findViewById<Button>(R.id.langue)
        btnlangue.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("LangueFragment")
            transaction.replace(R.id.fragment_container, LangueFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val btndons = view.findViewById<Button>(R.id.don)
        btndons.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("LesdonsFragment")
            transaction.replace(R.id.fragment_container, LesdonsFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val btndeco = view.findViewById<Button>(R.id.btn_deco)
        btndons.setOnClickListener{
            userViewModel.reinitialiserDonnees()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
            transaction.replace(R.id.fragment_container, SeConnecterFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        return view
    }

}