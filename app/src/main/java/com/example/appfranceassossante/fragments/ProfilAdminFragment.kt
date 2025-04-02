package com.example.appfranceassossante.fragments

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
        civ.text = userViewModel.civilite.value.toString() ?: ""

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

}