package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textToSpeech.AccessibilityPreferences
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import com.example.appfranceassossante.utilsAccessibilite.textToSpeech.SharedViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.TextSizeManager


class AccessibiliteFragment : BaseFragment() {
    private lateinit var spinner: Spinner
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var daltonismCheckbox: CheckBox
    private lateinit var daltonismRadioGroup: RadioGroup
    private lateinit var userViewModel: UserViewModel
    private lateinit var protanopie: RadioButton
    private lateinit var tritanopie: RadioButton
    private lateinit var deuteranopie: RadioButton
    private lateinit var checkboxSpeech: CheckBox


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_accessibilite, container, false)

        // initialise les vues
        spinner = view.findViewById(R.id.spinner_taille)
        daltonismCheckbox = view.findViewById(R.id.checkbox_daltonisme)
        daltonismRadioGroup = view.findViewById(R.id.group_daltonisme)
        protanopie = view.findViewById(R.id.radio_protanopie)
        deuteranopie = view.findViewById(R.id.radio_deuteranopie)
        tritanopie = view.findViewById(R.id.radio_tritanopie)
        checkboxSpeech = view.findViewById(R.id.checkbox_lecture)

        // restaures les états des checkbox / radio btn
        restoreSavedStates()

        // configure le spinner de la taille du texte
        setupSpinner()

        // checkbox daltonisme
        daltonismCheckbox.setOnCheckedChangeListener { _, checked ->
            AccessibilityPreferences.saveDaltonismEnabled(requireContext(), checked)
            updateRadioButtonsState(checked)

            if (!checked) {
                AccessibilityPreferences.saveDaltonismType(requireContext(), null.toString())
            }
        }

        // radio btn daltonisme
        daltonismRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.radio_protanopie -> "protanopie"
                R.id.radio_deuteranopie -> "deuteranopie"
                R.id.radio_tritanopie -> "tritanopie"
                else -> null
            }
            type?.let { AccessibilityPreferences.saveDaltonismType(requireContext(), it) }
        }

        // checkbox text to speech
        checkboxSpeech.setOnCheckedChangeListener(null)

        val isTtsEnabled = AccessibilityPreferences.getSpeechEnabled(requireContext())

        checkboxSpeech.isChecked = isTtsEnabled

        checkboxSpeech.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.enableSpeech(isChecked)
            AccessibilityPreferences.saveSpeechEnabled(requireContext(), isChecked)
        }

        // Observer les changements en temps réel du userviewmodel.handicap et ca va
        // activier les setOnCheckedChangeListener normalement
        userViewModel.handicap.observe(viewLifecycleOwner) { handicap ->
            setupSpinner()
            if(userViewModel.isUserLoggedIn()){
                if (handicap.equals(R.string.lecture))
                    checkboxSpeech.isChecked = true
                else if(handicap.equals(R.string.deuteranopie)){
                    daltonismCheckbox.isChecked = true
                    deuteranopie.isChecked = true
                }
                else if(handicap.equals(R.string.protanopie)){
                    daltonismCheckbox.isChecked = true
                    protanopie.isChecked = true
                }
                else if(handicap.equals(R.string.tritanopie)){
                    daltonismCheckbox.isChecked = true
                    tritanopie.isChecked = true
                }
                else if(handicap.equals(R.string.malvoyant)){
                    spinner.setSelection(3, false)
                }
            }
        }

        return view
    }

    private fun restoreSavedStates() {
        // restaure état de la checkbox du text to speech
        val isSpeechEnabled = AccessibilityPreferences.getSpeechEnabled(requireContext())
        view?.findViewById<CheckBox>(R.id.checkbox_lecture)?.isChecked = isSpeechEnabled

        // restaure état checkbox daltonisme
        val isDaltonismEnabled = AccessibilityPreferences.getDaltonismEnabled(requireContext())
        daltonismCheckbox.isChecked = isDaltonismEnabled
        updateRadioButtonsState(isDaltonismEnabled)

        // restaure radio btn daltonisme
        val daltonismType = AccessibilityPreferences.getDaltonismType(requireContext())
        daltonismType?.let {
            val radioId = when (it) {
                "protanopie" -> R.id.radio_protanopie
                "deuteranopie" -> R.id.radio_deuteranopie
                "tritanopie" -> R.id.radio_tritanopie
                else -> -1
            }
            if (radioId != -1) {
                daltonismRadioGroup.check(radioId)
            }
        }

        // restaure taille du texte
        val savedSize = AccessibilityPreferences.getTextSize(requireContext())
        TextSizeManager.sizeOffset = savedSize
    }

    private fun updateRadioButtonsState(enabled: Boolean) {
        for (i in 0 until daltonismRadioGroup.childCount) {
            (daltonismRadioGroup.getChildAt(i) as RadioButton).isEnabled = enabled
        }
    }

    private fun setupSpinner() {
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.text_sizes_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                        val offset = when (pos) {
                            0 -> 0f  // 18sp
                            1 -> 2f   // 20sp
                            2 -> 4f   // 22sp
                            3 -> 6f   // 24sp
                            else -> 0f
                        }
                        TextSizeManager.sizeOffset = offset
                        AccessibilityPreferences.saveTextSize(requireContext(), offset)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                // restaure selection du spinner
                val currentOffset = TextSizeManager.sizeOffset
                val position = when (currentOffset) {
                    0f -> 0
                    2f -> 1
                    4f -> 2
                    6f -> 3
                    else -> 0
                }
                spinner.setSelection(position, false)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val isSpeechEnabled = AccessibilityPreferences.getSpeechEnabled(requireContext())
        sharedViewModel.enableSpeech(isSpeechEnabled)
    }


}