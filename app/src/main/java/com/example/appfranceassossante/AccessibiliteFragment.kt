package com.example.appfranceassossante

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

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

        // Inflate the layout for this fragment
        return view
    }


}