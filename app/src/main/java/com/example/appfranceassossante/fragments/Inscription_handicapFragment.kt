package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
//import com.example.appfranceassossante.mongodb.MongoDBConnection

class Inscription_handicapFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    //private lateinit var mongoDBConnection: MongoDBConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        //mongoDBConnection = MongoDBConnection()

        val view = inflater.inflate(R.layout.fragment_inscription_handicap, container, false)
        val handicap = view.findViewById<Spinner>(R.id.handicap)
        val btnsinscrire = view.findViewById<Button>(R.id.sinscrire)

        btnsinscrire.setOnClickListener{
            val handic = handicap.toString()
            if(handic.isEmpty())
                Toast.makeText(context, getString(R.string.error_message_handicap), Toast.LENGTH_SHORT).show()
            else{
                userViewModel.setHandicap(handic) // Enregistre l'handicap
                saveUserToMongoDB()
            }
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

    private fun saveUserToMongoDB() {
        val userData = userViewModel.collectUserData()

        // Vérifiez que toutes les données nécessaires sont présentes
//        if (userData.values.all { it != null }) {
//            mongoDBConnection.saveUser(
//                userData["civilite"] ?: "",
//                userData["nom"] ?: "",
//                userData["prenom"] ?: "",
//                userData["email"] ?: "",
//                userData["mot de passe"] ?: "",
//                userData["handicap"] ?: ""
//            )

            Toast.makeText(context, R.string.message_inscription_reussie, Toast.LENGTH_SHORT).show()

            userViewModel.reinitialiserDonnees()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
            transaction.replace(R.id.fragment_container, SeConnecterFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
//        } else {
//            Toast.makeText(context, R.string.error_message_inscription_pas_reussi, Toast.LENGTH_SHORT).show()
//        }
    }

}