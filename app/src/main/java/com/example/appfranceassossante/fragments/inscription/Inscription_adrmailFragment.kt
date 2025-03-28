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
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetUserTask
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Inscription_adrmailFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private val getUserTask = GetUserTask()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_inscription_adrmail, container, false)
        val mail = view.findViewById<EditText>(R.id.adrmail)
        val btnsuivant = view.findViewById<Button>(R.id.suivant)

        btnsuivant.setOnClickListener{
            val mailSansEspace = mail.text.toString().trim()

            lifecycleScope.launch {
                try {
                    val user = getUserTask.getUserInBG(mailSansEspace)
                    when {
                        mailSansEspace.isEmpty() -> mail.error = getString(R.string.error_message_mail)
                        user != null -> showToast(getString(R.string.error_message_mail_existant))
                        else -> successfulLogin(mailSansEspace)
                    }
                } catch (e: Exception) {
                    Log.e("Login", getString(R.string.error_connexion), e)
                    showToast(getString(R.string.error_message_connexion))
                }
            }

            /*
            if(mailSansEspace.isEmpty()){
                mail.error = getString(R.string.error_message_mail)
            }
            //if(mongoDBConnection.isEmailAlreadyUsed(mailSansEspace))
              //  Toast.makeText(context, getString(R.string.error_message_mail_existant), Toast.LENGTH_SHORT).show()
            else {
                successfulLogin(mailSansEspace)
            }
            */
        }

        val btnretour = view.findViewById<Button>(R.id.retour)
        btnretour.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack() // retire le fragment actuel
        }

        val btnconnection = view.findViewById<Button>(R.id.connnection)
        btnconnection.setOnClickListener{
            fragmentNext(SeConnecterFragment()) // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
        }

        // Inflate the layout for this fragment
        return view

    }

    private fun successfulLogin(mail: String){
        userViewModel.setMail(mail) // Enregistre le mail
        fragmentNext(Inscription_confirmer_adrmailFragment()) // remplace le fragment actuel par le fragment qui suit ("Inscription_confirmer_adrmailFragment")
    }

    private fun fragmentNext(frag: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, frag)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}