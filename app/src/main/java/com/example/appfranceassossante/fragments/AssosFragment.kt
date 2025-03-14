package com.example.appfranceassossante.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.example.appfranceassossante.Assos
import com.example.appfranceassossante.AssosAdapter
import com.example.appfranceassossante.R

class AssosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_assos, container, false)

        val gridView : GridView = view.findViewById(R.id.grilleAssos)

        val assosListe = listOf(
            Assos("France Dépression", R.drawable.francedepressionassos),
            Assos("Test2", R.drawable.apay)
        )

        val adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)

        gridView.adapter = adapter
        return view
    }
}