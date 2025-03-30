package com.example.appfranceassossante.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.utilsAccessibilite.SharedViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale


class AccueilFragment : BaseFragment() {
    private lateinit var sharedViewModel: SharedViewModel // pour la communiquer avec le fragment accessibilité (pour les btn speech)
    private lateinit var btnSpeech1: Button
    private lateinit var btnSpeech2: Button
    private var textToSpeech: TextToSpeech? = null
    private var isSpeaking = false // pour arrêter le text to speech
    private var currentSpeakingButton: Button? = null
    private var currentLanguage: String = "fr" // pour obtenir la langue actuelle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_accueil, container, false)

        val btn_don = view.findViewById<Button>(R.id.btn_don)
        btn_don.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_don // selectionne "don" dans le menu
        }

        val btn_assos = view.findViewById<Button>(R.id.btn_assos)
        btn_assos.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_assoc // selectionne "assos"
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSpeech1 = view.findViewById(R.id.btnSpeech)
        btnSpeech2 = view.findViewById(R.id.btnSpeech2)

        // change la visibilité des btn dépendants de si la checkbox est checked ou pas
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.isSpeechEnabled.observe(viewLifecycleOwner) { isEnabled ->
            btnSpeech1.visibility = if (isEnabled) View.VISIBLE else View.GONE
            btnSpeech2.visibility = if (isEnabled) View.VISIBLE else View.GONE

            if (!isEnabled) {
                textToSpeech?.stop()
                resetButtons()
            }
        }

        // initialise le text to speech
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setTtsLanguage() // choisis la bonne langue (fr, en, zh)
                setupUtteranceListener()
            }
        }
    }

    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            // change le texte du btn quand il termine de lire
            override fun onDone(utteranceId: String?) {
                activity?.runOnUiThread {
                    if (utteranceId == "last_utterance") {
                        resetButtons()
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                activity?.runOnUiThread {
                    resetButtons()
                }
            }
        })
    }

    private fun setTtsLanguage() {
        // langue du text to speech
        val language = Locale.getDefault().language
        val locale = when (language) {
            "en" -> Locale.US
            "zh" -> Locale.SIMPLIFIED_CHINESE
            else -> Locale.FRENCH
        }

        when (textToSpeech?.setLanguage(locale)) {
            // jsp ça fait quoi mais si on enlève ça marche plus
            TextToSpeech.LANG_MISSING_DATA -> handleMissingLanguageData()
            TextToSpeech.LANG_NOT_SUPPORTED -> fallbackToEnglish()
        }
    }

    private fun handleMissingLanguageData() {
        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
        startActivity(installIntent)
        textToSpeech?.setLanguage(Locale.US)
    }

    private fun fallbackToEnglish() {
        textToSpeech?.setLanguage(Locale.US)
    }


    override fun onResume() {
        super.onResume()
        setupSpeechButtons()
        checkAndUpdateLanguage()
    }

    private fun setupSpeechButtons() {
        // Bouton 1
        val texts1 = listOf(
            requireView().findViewById<TextView>(R.id.tvAccueilSupport),
            requireView().findViewById<TextView>(R.id.tvAccueilSupport2),
            requireView().findViewById<TextView>(R.id.tvAccueilSupport3),
            requireView().findViewById<TextView>(R.id.tvAccueilSupport4),
            requireView().findViewById<Button>(R.id.btn_don)
        )
        setupButton(btnSpeech1, texts1)

        // Bouton 2
        val texts2 = listOf(
            requireView().findViewById<TextView>(R.id.tvAccueilAssos),
            requireView().findViewById<TextView>(R.id.tvAccueilAssos2),
            requireView().findViewById<Button>(R.id.btn_assos)
        )
        setupButton(btnSpeech2, texts2)
    }

    private fun setupButton(button: Button, textViews: List<View>) {
        button.text = getString(R.string.lecture)
        button.setOnClickListener {
            if (isSpeaking && currentSpeakingButton == button) {
                stopSpeech(button)
            } else {
                startSpeech(button, textViews)
            }
        }
    }

    private fun startSpeech(button: Button, textViews: List<View>) {
        textToSpeech?.stop()
        currentSpeakingButton = button

        textViews.forEachIndexed { index, view ->
            val text = when (view) {
                is TextView -> view.text.toString()
                is Button -> view.text.toString()
                else -> ""
            }

            if (text.isNotBlank()) {
                val isLast = index == textViews.size - 1
                textToSpeech?.speak(
                    text,
                    if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
                    null,
                    if (isLast) "last_utterance" else "utt_$index"
                )
            }
        }

        isSpeaking = true
        button.text = getString(R.string.stop)
    }

    private fun resetButtons() {
        isSpeaking = false
        currentSpeakingButton?.text = getString(R.string.lecture)
        currentSpeakingButton = null
    }

    private fun stopSpeech(button: Button) {
        textToSpeech?.stop()
        isSpeaking = false
        currentSpeakingButton = null
        button.text = getString(R.string.lecture)
    }

    private fun checkAndUpdateLanguage() {
        val newLanguage = Locale.getDefault().language
        if (newLanguage != currentLanguage) {
            currentLanguage = newLanguage
            setTtsLanguage()
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
