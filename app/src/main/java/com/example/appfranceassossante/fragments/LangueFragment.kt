import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.MainActivity
import com.example.appfranceassossante.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class LangueFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_langue, container, false)


        val btnretour = view.findViewById<Button>(R.id.btnretour)
        btnretour.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack() // retire le fragment actuel
        }

        val radioGroup = view.findViewById<RadioGroup>(R.id.langue)
        val radiofr = view.findViewById<RadioButton>(R.id.fr)
        val radioen = view.findViewById<RadioButton>(R.id.en)
        val radiozh = view.findViewById<RadioButton>(R.id.zh)

        when (getSavedLanguage(requireContext())) {
            "fr" -> radiofr.isChecked = true
            "en" -> radioen.isChecked = true
            "zh" -> radiozh.isChecked = true
        }

        //change la langue avec setLocale
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.fr -> setLocale("fr")
                R.id.en -> setLocale("en")
                R.id.zh -> setLocale("zh")
            }
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun setLocale(languageCode: String) {
        saveLanguage(requireContext(), languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)

        // Met à jour le contexte des ressources
//        requireActivity().apply {
//            resources.updateConfiguration(config, resources.displayMetrics)
//        }

        // selectionne l'item accueil dans le menu
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.selectedItemId = R.id.navigation_accueil

        // Redessine l'UI pour appliquer les changements de langue
        requireActivity().recreate()
    }

    //garde la langue choisie meme apres fermeture de l'app
    private fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }

    //recupere la langue
    private fun getSavedLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", "fr") ?: "fr"
    }
}