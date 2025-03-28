package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import com.example.appfranceassossante.utilsTextSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.utilsTextSize.TextSizeManager


class AccessibiliteFragment : BaseFragment() {
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //spinner changement de taille du texte
        val view = inflater.inflate(R.layout.fragment_accessibilite, container, false)
        spinner = view.findViewById<Spinner>(R.id.spinner_taille)
        val taille = arrayOf(18, 20, 22, 24)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taille)

        setupSpinner()
        restoreSpinnerSelection()

        //btn daltonisme
        val radioBtn = view.findViewById<RadioGroup>(R.id.group_daltonisme)
        val dalto = view.findViewById<CheckBox>(R.id.checkbox_daltonisme)

        dalto.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { checkBox, checked ->
            for (i in 0 until radioBtn.childCount) {
                (radioBtn.getChildAt(i) as RadioButton).isEnabled = checked //"checked" c'est le statut de la checkbox, si true radiobutton actives, si false radiobutton desactives
                if(!checked){
                    (radioBtn.getChildAt(i) as RadioButton).isChecked = false //si la checkbox de daltonisme n'est pas cochee, les radiobuttons ne le sont pas non plus
                }
            }
        })

        //set default to false
        for (i in 0 until radioBtn.childCount) {
            (radioBtn.getChildAt(i) as RadioButton).isEnabled = false
        }

        // Inflate the layout for this fragment
        return view
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
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }
    }

    private fun restoreSpinnerSelection() {
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