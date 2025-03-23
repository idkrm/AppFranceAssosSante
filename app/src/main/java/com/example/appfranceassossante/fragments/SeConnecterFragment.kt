package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
//import com.example.appfranceassossante.mongodb.MongoDBConnection



class SeConnecterFragment : Fragment() {

    //private lateinit var mongoDBConnection : MongoDBConnection
    private lateinit var userViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //mongoDBConnection = MongoDBConnection()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_se_connecter, container, false)

        val btnconnecter = view.findViewById<Button>(R.id.se_connecter)
        val mail = view.findViewById<EditText>(R.id.co_mail)
        val mdp = view.findViewById<EditText>(R.id.co_mdp)

        btnconnecter.setOnClickListener {
            val mail = mail.text.toString()
            val motDePasse = mdp.text.toString()

            if (mail.isEmpty() || motDePasse.isEmpty()) {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_champs_vides),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //var user = mongoDBConnection.findUserByEmail(mail)

//                if (user != null) {
//                    if (motDePasse.equals(user.mdp)) {
//                        userViewModel.setCivilite(user.civilite)
//                        userViewModel.setNom(user.nom)
//                        userViewModel.setPrenom(user.prenom)
//                        userViewModel.setMail(user.email)
//                        userViewModel.setMdp(user.mdp)
//                        userViewModel.setHandicap(user.handicap)
                        val transaction =
                            requireActivity().supportFragmentManager.beginTransaction()
                        // remplace le fragment actuel par le fragment qui suit ("ProfilFragment")
                        transaction.replace(R.id.fragment_container, ProfilFragment())
                        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
                        transaction.commit()
//                    } else
//                        Toast.makeText(
//                            context,
//                            getString(R.string.error_message_mdp_incorrect),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                } else
//                    Toast.makeText(
//                        context,
//                        getString(R.string.error_message_user_non_existant),
//                        Toast.LENGTH_SHORT
//                    ).show()
            }
        }

        val btnsinscrire = view.findViewById<Button>(R.id.sinscrire)
        btnsinscrire.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("InscriptionFragment")
            transaction.replace(R.id.fragment_container, InscriptionFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }
        return view
    }
}