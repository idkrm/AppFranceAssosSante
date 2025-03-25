package com.example.appfranceassossante.fragments

import CreateUserTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch

//import com.example.appfranceassossante.mongodb.MongoDBConnection

class Inscription_handicapFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var createUserTask: CreateUserTask

    private lateinit var handicap: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        createUserTask = CreateUserTask(requireContext())

        val view = inflater.inflate(R.layout.fragment_inscription_handicap, container, false)

        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        handicap = view.findViewById(R.id.handicap)

        val btnsinscrire = view.findViewById<Button>(R.id.sinscrire)
        btnsinscrire.setOnClickListener {
            signUpClicked()
        }

        val btnconnection = view.findViewById<Button>(R.id.connnection)
        btnconnection.setOnClickListener {
            connectClicked()
        }
    }

    private fun signUpClicked(){
        val handic = handicap.selectedItem.toString()
        when {
            handic.isEmpty() -> showToast(R.string.error_message_handicap)
            else -> {userViewModel.setHandicap(handic)
            saveUserToDatabase()}
        }
    }

    private fun connectClicked(){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
        transaction.replace(R.id.fragment_container, SeConnecterFragment())
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private fun saveUserToDatabase() {
        val userData = userViewModel.collectUserData()
        // Vérification des champs vides
        if (userData.civilite.isEmpty() || userData.nom.isEmpty() || userData.prenom.isEmpty() ||
            userData.email.isEmpty() || userData.mdp.isEmpty() || userData.handicap.isEmpty()) {
            showToast(R.string.error_message_champs_vides)
            return
        }
        lifecycleScope.launch {
            try {
                val userCreated = createUserTask.createUserInBG(userData)
                if (userCreated) {
                    showToast(R.string.message_inscription_reussie)
                    userViewModel.reinitialiserDonnees()
                    navigateToLogin()
                } else {
                    showToast(R.string.error_message_inscription_pas_reussi)
                }
            } catch (e: Exception) {
                showToast(R.string.error_message_connexion)
            }
        }
    }


    private fun navigateToLogin() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
        transaction.replace(R.id.fragment_container, SeConnecterFragment())
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(
            requireContext(),
            getString(messageResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}