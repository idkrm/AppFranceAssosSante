package com.example.appfranceassossante.fragments

import CreateUserTask
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
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.User
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch

//import com.example.appfranceassossante.mongodb.MongoDBConnection



class SeConnecterFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var createUserTask: CreateUserTask

    private lateinit var mail: EditText
    private lateinit var mdp: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        createUserTask = CreateUserTask(requireContext())

        val view = inflater.inflate(R.layout.fragment_se_connecter, container, false)

        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        mail = view.findViewById(R.id.co_mail)
        mdp = view.findViewById(R.id.co_mdp)

        val btnconnecter = view.findViewById<Button>(R.id.se_connecter)
        btnconnecter.setOnClickListener {
            LoginClicked()
        }
        val btnsinscrire = view.findViewById<Button>(R.id.sinscrire)
        btnsinscrire.setOnClickListener {
            signUpClicked()
        }
    }

    private fun LoginClicked() {
        val mail = mail.text.toString()
        val motDePasse = mdp.text.toString()

        when {
            mail.isEmpty() || motDePasse.isEmpty() ->
                showToast(R.string.error_message_champs_vides)

            else -> tryLogin(mail, motDePasse)
        }
    }

    private fun tryLogin(email: String, motdp: String) {
        lifecycleScope.launch {
            try {
                val user = createUserTask.findUserByMail(email)
                when {
                    user == null -> showToast(R.string.error_message_user_non_existant)
                    !motdp.equals(user.mdp) -> showToast(R.string.error_message_mdp_incorrect)
                    else -> successfulLogin(user)
                }
            } catch (e: Exception) {
                Log.e("Login", R.string.error_connexion.toString(), e)
                showToast(R.string.error_message_connexion)
            }
        }
    }

    private fun successfulLogin(user: User) {
        userViewModel.updateUserData(user)
        val fragment = when (user.role) {
            "administrateur" -> ProfilAdminFragment() // remplace le fragment actuel par le fragment qui suit ("ProfilAdminFragment")
            else -> ProfilFragment() // remplace le fragment actuel par le fragment qui suit ("ProfilFragment")
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private fun signUpClicked() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("InscriptionFragment")
        transaction.replace(R.id.fragment_container, InscriptionFragment())
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