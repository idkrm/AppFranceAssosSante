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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.don.DonTypeFragment
import com.example.appfranceassossante.fragments.inscription.InscriptionFragment
import com.example.appfranceassossante.models.User
import com.example.appfranceassossante.models.UserViewModel
import com.example.appfranceassossante.utilsAccessibilite.AccessibilityPreferences
import com.example.appfranceassossante.utilsAccessibilite.ColorBlindnessFilter
import com.example.appfranceassossante.utilsAccessibilite.SharedViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.TextSizeManager
import kotlinx.coroutines.launch

class SeConnecterFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private val getUserTask = GetUserTask()
    private lateinit var sharedViewModel: SharedViewModel

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
            loginClicked()
        }
        val btnsinscrire = view.findViewById<Button>(R.id.sinscrire)
        btnsinscrire.setOnClickListener {
            signUpClicked()
        }
    }

    private fun loginClicked() {
        val email = this.mail.text.toString()
        val motDePasse = this.mdp.text.toString()

        if (email.isEmpty() || motDePasse.isEmpty()) {
            showToast(R.string.error_message_champs_vides)
            return
        }

        tryLogin(email, motDePasse)
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
        userViewModel.setUserLoggedIn(true)
        userViewModel.updateUserData(user)
        userViewModel.handicap.observe(viewLifecycleOwner) { handicap ->
            if (!handicap.isNullOrBlank()) {
                when (handicap) {
                    getString(R.string.lecture) -> {
                        AccessibilityPreferences.saveSpeechEnabled(requireContext(), true)
                        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
                        sharedViewModel.enableSpeech(true)
                    }
                    getString(R.string.malvoyant) -> {
                        AccessibilityPreferences.saveTextSize(requireContext(), 6f)
                        TextSizeManager.sizeOffset = 6f
                    }
                    getString(R.string.protanopie) -> {
                        AccessibilityPreferences.saveDaltonismEnabled(requireContext(), true)
                        AccessibilityPreferences.saveDaltonismType(requireContext(), "protanopie")
                        ColorBlindnessFilter.applyFilter(requireActivity().window, "protanopie")
                    }
                    getString(R.string.deuteranopie) -> {
                        AccessibilityPreferences.saveDaltonismEnabled(requireContext(), true)
                        AccessibilityPreferences.saveDaltonismType(requireContext(), "deuteranopie")
                        ColorBlindnessFilter.applyFilter(requireActivity().window, "deuteranopie")
                    }
                    getString(R.string.tritanopie) -> {
                        AccessibilityPreferences.saveDaltonismEnabled(requireContext(), true)
                        AccessibilityPreferences.saveDaltonismType(requireContext(), "tritanopie")
                        ColorBlindnessFilter.applyFilter(requireActivity().window, "tritanopie")
                    }
                }
            }
        }
        val returnToDonType = arguments?.getBoolean("returnToDonType", false) ?: false
        val fragment = if (returnToDonType) {
            DonTypeFragment()// remplace le fragment actuel par le fragment qui suit ("ProfilFragment")
        } else if(user.admin == null){
            ProfilFragment()
        }
        else {
            ProfilAdminFragment() // remplace le fragment actuel par le fragment qui suit ("ProfilAdminFragment")
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
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