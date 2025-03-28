package com.example.appfranceassossante.fragments

import com.example.appfranceassossante.apiService.GetUserTask
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
import com.example.appfranceassossante.fragments.inscription.InscriptionFragment
import com.example.appfranceassossante.models.User
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch

class SeConnecterFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private val getUserTask = GetUserTask()

    private lateinit var mail: EditText
    private lateinit var mdp: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

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
        val mail = this.mail.text.toString()
        val motDePasse = this.mdp.text.toString()

        if (mail.isEmpty() || motDePasse.isEmpty()) {
            showToast(R.string.error_message_champs_vides)
            return
        }

        tryLogin(mail, motDePasse)
    }

    private fun tryLogin(email: String, motdp: String) {
        lifecycleScope.launch {
            try {
                val user = getUserTask.getUserInBG(email)
                when {
                    user == null -> showToast(R.string.error_message_user_non_existant)
                    motdp != (user.mdp) -> showToast(R.string.error_message_mdp_incorrect)
                    else -> successfulLogin(user)
                }
            } catch (e: Exception) {
                Log.e("Login", getString(R.string.error_connexion), e)
                showToast(R.string.error_message_connexion)
            }
        }
    }

    private fun successfulLogin(user: User) {
        userViewModel.updateUserData(user)
        userViewModel.setUserLoggedIn(true)
        val fragment = if (user.admin == null) {
            ProfilFragment() // remplace le fragment actuel par le fragment qui suit ("ProfilFragment")
        } else {
            ProfilAdminFragment() // remplace le fragment actuel par le fragment qui suit ("ProfilAdminFragment")
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