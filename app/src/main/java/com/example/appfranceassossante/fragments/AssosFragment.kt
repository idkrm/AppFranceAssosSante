package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SearchView
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.AssosAdapter
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosTask

class AssosFragment : Fragment() {
    private lateinit var assosListe: MutableList<Assos>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_assos, container, false)

        val gridView : GridView = view.findViewById(R.id.grilleAssos)

        assosListe = mutableListOf()

        // Pour récuperer les assos de la bd
        val getAssosTask = GetAssosTask { associations ->
            // Vérifier si la liste n'est pas vide
            if (associations.isNotEmpty()) {
                // Ajouter les associations récupérées à la liste assosListe
                assosListe.addAll(associations)

                // Afficher les associations dans le logcat
                associations.forEach { association ->
                    Log.d("MainActivity", "Association récupérée: ${association.getAssosName()}, ${association.getAcronyme()}")
                }

                // Mettre à jour l'adaptateur avec la liste d'associations
                val adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)
                gridView.adapter = adapter
            } else {
                Log.d("MainActivity", "Aucune association trouvée")
            }
        }
        getAssosTask.execute()

        val adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)

        gridView.adapter = adapter
//        setDynamicHeight(gridView, 3)

        val searchV : SearchView = view.findViewById(R.id.search_bar)
        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)

                return false
            }

        })
        return view
    }

//    fun setDynamicHeight(gridView: GridView, columns: Int) {
//        val listAdapter = gridView.adapter ?: return
//
//        var totalHeight = 0
//        val items = listAdapter.count
//        val rows = Math.ceil((items / columns.toDouble())).toInt()
//
//        for (i in 0 until rows) {
//            val listItem = listAdapter.getView(i, null, gridView)
//            listItem.measure(0, 0)
//            totalHeight += listItem.measuredHeight
//        }
//
//        val params = gridView.layoutParams
//        params.height = totalHeight + (gridView.verticalSpacing * (rows - 1))
//        gridView.layoutParams = params
//        gridView.requestLayout()
//    }

//    fun setAdapterFilter(Text : String) : String {
//
//    }
}