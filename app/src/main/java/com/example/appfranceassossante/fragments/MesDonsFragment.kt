package com.example.appfranceassossante.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.appfranceassossante.R

class MesDonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_dons, container, false)
        val spinner = view.findViewById<Spinner>(R.id.histodon)
        val filtre = arrayOf("Croissant", "Décroissant")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filtre)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val btn = view.findViewById<Button>(R.id.btnretour)
        btn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
        // Inflate the layout for this fragment
        return view
    }


}