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

class LesdonsFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_les_dons, container, false)

        val nomassos = view.findViewById<TextView>(R.id.nomassociation)
        nomassos.text = userViewModel.admin..toString()



        val btnlangue = view.findViewById<Button>(R.id.langue)
        btnlangue.setOnClickListener{
            fragmentRemplace(LangueFragment()) // remplace le fragment actuel par le fragment qui suit ("LangueFragment")
        }

        val btndons = view.findViewById<Button>(R.id.don)
        btndons.setOnClickListener{
            fragmentRemplace(LesdonsFragment()) // remplace le fragment actuel par le fragment qui suit ("LesdonsFragment")
        }

        val btndeco = view.findViewById<Button>(R.id.btn_deco)
        btndeco.setOnClickListener{
            userViewModel.reinitialiserDonnees()
            fragmentRemplace(SeConnecterFragment()) // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
        }

        val btnlegacy = view.findViewById<Button>(R.id.legacy)
        btnlegacy.setOnClickListener{

        }

        val btnpolicy = view.findViewById<Button>(R.id.policy)
        btnpolicy.setOnClickListener{

        }

        val btncondition = view.findViewById<Button>(R.id.condition)
        btncondition.setOnClickListener{

        }

        return view
    }

    private fun fragmentRemplace(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("fragment")
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

}