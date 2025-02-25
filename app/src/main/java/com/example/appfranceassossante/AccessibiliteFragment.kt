package com.example.appfranceassossante

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment


class AccessibiliteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_accessibilite, container, false)
        val spinner = view.findViewById<Spinner>(R.id.spinner_taille)
        val taille = arrayOf(18, 20, 22, 24, 26)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taille)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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


}