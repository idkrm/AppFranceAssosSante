
package com.example.appfranceassossante.fragments

import LangueFragment
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
                Toast.makeText(requireContext(), getString(R.string.pbmail), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Boîte de dialogue pour confirmation
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.confirm))
                setMessage(getString(R.string.deleteaccount))
                setPositiveButton(getString(R.string.delete)) { _, _ ->
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
                setNegativeButton(getString(R.string.cancel), null)
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

        tvCivilite.text = getCiviliteTranslation(civilite)

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

    private fun getCiviliteTranslation(civilite: String?): String {
        val currentLanguage = Locale.getDefault().language

        val civiliteMap = mapOf(
            "fr" to mapOf(
                "Monsieur" to "Monsieur",
                "Madame" to "Madame",
                "Ne souhaite pas répondre" to "Ne souhaite pas répondre",
                "Autre" to "Autre",
                "先生" to "Monsieur", // Traduction de "Mr" en chinois vers le français
                "女士" to "Madame", // Traduction de "Mrs" en chinois vers le français
                "不想回答" to "Ne souhaite pas répondre", // Traduction de "Do not wish to answer" en chinois vers le français
                "其他" to "Autre", // Traduction de "Other" en chinois vers le français
                "Mr" to "Monsieur", // Traduction de "Mr" en anglais vers le français
                "Mrs" to "Madame", // Traduction de "Mrs" en anglais vers le français
                "Do not wish to answer" to "Ne souhaite pas répondre", // Traduction de "Do not wish to answer" en anglais vers le français
                "Other" to "Autre" // Traduction de "Other" en anglais vers le français
            ),
            "zh" to mapOf(
                "Monsieur" to "先生", // Traduction de "Monsieur" en français vers le chinois
                "Madame" to "女士", // Traduction de "Madame" en français vers le chinois
                "Ne souhaite pas répondre" to "不想回答", // Traduction de "Ne souhaite pas répondre" en français vers le chinois
                "Autre" to "其他", // Traduction de "Autre" en français vers le chinois
                "先生" to "先生", // "先生" reste "先生" en chinois
               "女士" to "女士", // "女士" reste "女士" en chinois
                "不想回答" to "不想回答", // "不想回答" reste "不想回答" en chinois
                "其他" to "其他", // "其他" reste "其他" en chinois
                "Mr" to "先生", // Traduction de "Mr" en anglais vers le chinois
                "Mrs" to "女士", // Traduction de "Mrs" en anglais vers le chinois
                "Do not wish to answer" to "不想回答", // Traduction de "Do not wish to answer" en anglais vers le chinois
                "Other" to "其他" // Traduction de "Other" en anglais vers le chinois
            ),
            "en" to mapOf(
                "Monsieur" to "Mr", // Traduction de "Monsieur" en français vers l'anglais
                "Madame" to "Mrs", // Traduction de "Madame" en français vers l'anglais
                "Ne souhaite pas répondre" to "Do not wish to answer", // Traduction de "Ne souhaite pas répondre" en français vers l'anglais
                "Autre" to "Other", // Traduction de "Autre" en français vers l'anglais
                "先生" to "Mr", // Traduction de "先生" en chinois vers l'anglais
                "女士" to "Mrs", // Traduction de "女士" en chinois vers l'anglais
                "不想回答" to "Do not wish to answer", // Traduction de "不想回答" en chinois vers l'anglais
                "其他" to "Other", // Traduction de "其他" en chinois vers l'anglais
                "Mr" to "Mr", // "Mr" reste "Mr" en anglais
                "Mrs" to "Mrs", // "Mrs" reste "Mrs" en anglais
                "Do not wish to answer" to "Do not wish to answer", // "Do not wish to answer" reste "Do not wish to answer" en anglais
                "Other" to "Other" // "Other" reste "Other" en anglais
            )
        )

        // Retourne la traduction selon la langue courante
        return civiliteMap[currentLanguage]?.get(civilite) ?: civiliteMap["fr"]?.get(civilite) ?: civilite ?: ""
    }
}