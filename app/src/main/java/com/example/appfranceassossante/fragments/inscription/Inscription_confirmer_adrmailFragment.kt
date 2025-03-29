package com.example.appfranceassossante.fragments.inscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.UserViewModel

class Inscription_confirmer_adrmailFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_inscription_confirmer_adrmail, container, false)
        val confirmerMail = view.findViewById<EditText>(R.id.confirmer_adrmail)
        val btnsuivant = view.findViewById<Button>(R.id.suivant)
        val mailSaisi = userViewModel.mail.value ?: ""

        btnsuivant.setOnClickListener{
            val mailSansEspace = confirmerMail.text.toString().trim()

            if(mailSansEspace.isEmpty())
                confirmerMail.error = getString(R.string.error_message_confirmer_mail)
            else if(mailSansEspace != mailSaisi)
                Toast.makeText(context, getString(R.string.error_message_confirmer_mail_saisi), Toast.LENGTH_SHORT).show()
            else{
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                // remplace le fragment actuel par le fragment qui suit ("Inscription_mdpFragment")
                transaction.replace(R.id.fragment_container, Inscription_mdpFragment())
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