package com.example.appfranceassossante.fragments.inscription

import com.example.appfranceassossante.apiService.CreateUserTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch

class Inscription_handicapFragment : BaseFragment() {

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

        val handicapOptions = listOf(getString(R.string.pashandicap), getString(R.string.lecture),getString(R.string.malvoyant))

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, handicapOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        handicap.adapter = adapter

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
        val handic = handicap.selectedItem?.toString()?.trim()
        //ICI
        if (handic.isNullOrBlank()) {
            showToast(R.string.error_message_handicap)
        } else {
            userViewModel.setHandicap(handic)
            saveUserToDatabase()
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