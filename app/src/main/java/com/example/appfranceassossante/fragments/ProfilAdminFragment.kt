package com.example.appfranceassossante.fragments

import LangueFragment
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import com.example.appfranceassossante.utilsAccessibilite.AccessibilityPreferences
import com.example.appfranceassossante.utilsAccessibilite.ColorBlindnessFilter
import com.example.appfranceassossante.utilsAccessibilite.SharedViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.TextSizeManager
import java.util.Locale

class ProfilAdminFragment : BaseFragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var civ: TextView
    private lateinit var nom: TextView
    private lateinit var prenom: TextView
    private lateinit var mail: TextView
    private lateinit var assos: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil_admin, container, false)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        civ = view.findViewById(R.id.civilitepersonne)
        civ.text = getCiviliteTranslation(userViewModel.civilite.value.toString())

        nom = view.findViewById(R.id.nompersonne)
        nom.text = userViewModel.nom.value.toString().uppercase() ?: ""


        prenom = view.findViewById(R.id.prenompersonne)
        prenom.text = userViewModel.prenom.value.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT)
        else it.toString() } ?: ""

        mail = view.findViewById(R.id.mailpersonne)
        mail.text = userViewModel.mail.value.toString() ?: ""

        assos = view.findViewById(R.id.assospersonne)
        assos.text = userViewModel.admin.value?.getAssosName().toString() ?: "Aucune association"

        val flagDrawable = langueFlag() // met le bon drapeau
        flagDrawable.setBounds(0, 0, 50, 50)

        val btnlangue = view.findViewById<Button>(R.id.langue)
        btnlangue.setCompoundDrawablesWithIntrinsicBounds(flagDrawable, null, null, null)

        btnlangue.setOnClickListener{
            fragmentRemplace(LangueFragment()) // remplace le fragment actuel par le fragment qui suit ("LangueFragment")
        }

        val btndons = view.findViewById<Button>(R.id.don)
        btndons.setOnClickListener{
            fragmentRemplace(LesdonsFragment()) // remplace le fragment actuel par le fragment qui suit ("LesdonsFragment")
        }

        val btndeco = view.findViewById<Button>(R.id.btn_deco)
        btndeco.setOnClickListener{
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

            // pas utilisé la méthode fragmentRemplace pcq faut pas l'ajouter au backstack
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SeConnecterFragment())
            transaction.commit()
        }

        return view
    }

    private fun fragmentRemplace(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("fragment")
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private fun langueFlag() : Drawable {
        //changer le drapeau en fonction de la langue
        val currentLanguage = Locale.getDefault().language
        val flagDrawable = when (currentLanguage) {
            "fr" -> resizeDrawable(R.drawable.fr,150,60)
            "en" -> resizeDrawable(R.drawable.gb,150,60)
            "zh" -> resizeDrawable(R.drawable.chine,150,60)
            else -> resizeDrawable(R.drawable.fr,150,60)
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