
package com.example.appfranceassossante.fragments

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import java.util.Locale
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable
import com.example.appfranceassossante.apiService.DeleteUserTask
import com.example.appfranceassossante.utilsAccessibilite.AccessibilityPreferences
import com.example.appfranceassossante.utilsAccessibilite.ColorBlindnessFilter
import com.example.appfranceassossante.utilsAccessibilite.SharedViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.utilsAccessibilite.textSize.TextSizeManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfilFragment : BaseFragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userViewModel : UserViewModel
    private lateinit var tvCivilite: TextView
    private lateinit var tvNom: TextView
    private lateinit var tvPrenom: TextView
    private lateinit var tvMail: TextView
    private lateinit var btnDeco: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        tvCivilite = view.findViewById(R.id.civilitepersonne)
        tvNom = view.findViewById(R.id.nompersonne)
        tvPrenom = view.findViewById(R.id.prenompersonne)
        tvMail = view.findViewById(R.id.mailpersonne)

        showUserInfo() // remplis les text view des infos du user

        val flagDrawable = langueFlag() // met le bon drapeau
        flagDrawable.setBounds(0, 0, 50, 50)
        val btnlangue = view.findViewById<Button>(R.id.langue)
        btnlangue.setCompoundDrawablesWithIntrinsicBounds(flagDrawable, null, null, null)

        btnlangue.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("LangueFragment")
            transaction.replace(R.id.fragment_container, LangueFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val btnhistodon = view.findViewById<Button>(R.id.don)
        btnhistodon.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("MesDonsFragment")
            transaction.replace(R.id.fragment_container, MesDonsFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val btnSupprUser = view.findViewById<Button>(R.id.btnSuppr)
        btnSupprUser.setOnClickListener {
            val userEmail = userViewModel.mail.value

            if (userEmail == null) {
                Toast.makeText(requireContext(), "Problème de récupération du mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Boîte de dialogue pour confirmation
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Confirmation")
                setMessage("Voulez-vous vraiment supprimer votre compte ?")
                setPositiveButton("Supprimer") { _, _ ->
                    DeleteUserTask(requireContext()).execute(userEmail)

                    userViewModel.reinitialiserDonnees()
                    userViewModel.setUserLoggedIn(false)

                    // enleve toutes les options d'accessibilité
                    AccessibilityPreferences.saveSpeechEnabled(requireContext(), false)
                    sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
                    sharedViewModel.enableSpeech(false)

                    AccessibilityPreferences.saveDaltonismEnabled(requireContext(), false)
                    AccessibilityPreferences.saveDaltonismType(requireContext(), "")
                    ColorBlindnessFilter.applyFilter(requireActivity().window, "")

                    AccessibilityPreferences.saveTextSize(requireContext(), 0f)
                    TextSizeManager.sizeOffset = 0f
                    // FIN

                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, SeConnecterFragment())
                    transaction.commit()
                }
                setNegativeButton("Annuler", null)
            }.show()
        }

        btnDeco = view.findViewById(R.id.btn_deco)
        btnDeco.setOnClickListener{
            userViewModel.reinitialiserDonnees()
            userViewModel.setUserLoggedIn(false)

            // enleve toutes les options d'accessibilité
            AccessibilityPreferences.saveSpeechEnabled(requireContext(), false)
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            sharedViewModel.enableSpeech(false)

            AccessibilityPreferences.saveDaltonismEnabled(requireContext(), false)
            AccessibilityPreferences.saveDaltonismType(requireContext(), "")
            ColorBlindnessFilter.applyFilter(requireActivity().window, "")

            AccessibilityPreferences.saveTextSize(requireContext(), 0f)
            TextSizeManager.sizeOffset = 0f
            // FIN

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SeConnecterFragment())
            transaction.commit()
        }

        return view
    }

    private fun showUserInfo(){
        val civilite = userViewModel.civilite.value
        val nom = userViewModel.nom.value
        val prenom = userViewModel.prenom.value
        val mail = userViewModel.mail.value

        tvCivilite.text = civilite

        if (nom != null) {
            tvNom.text = nom.uppercase() // mets le nom en CAPS
        }

        if (prenom != null) { // capitalize la premiere lettre
            tvPrenom.text = prenom.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                                        else it.toString() }
        }
        tvMail.text = mail
    }

    private fun langueFlag() : Drawable{
        //changer le drapeau en fonction de la langue
        val currentLanguage = Locale.getDefault().language
        val flagDrawable = when (currentLanguage) {
            "fr" -> resizeDrawable(R.drawable.fr,100,60)
            "en" -> resizeDrawable(R.drawable.gb,100,60)
            "zh" -> resizeDrawable(R.drawable.chine,100,60)
            else -> resizeDrawable(R.drawable.fr,100,60)
        }
        return flagDrawable
    }

    fun resizeDrawable(drawableId: Int, newWidth: Int, newHeight: Int): Drawable {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        val resizedBitmap = bitmap.scale(newWidth, newHeight, false)
        return resizedBitmap.toDrawable(resources)
    }
}