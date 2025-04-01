package com.example.appfranceassossante.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.SearchView
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.AssosAdapter
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosByFilterTask
import com.example.appfranceassossante.apiService.GetAssosTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AssosFragment : BaseFragment() {
    private lateinit var adapter: AssosAdapter
    private lateinit var assosListe: MutableList<Assos>
    private lateinit var gridView: GridView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view : View = inflater.inflate(R.layout.fragment_assos, container, false)

        gridView = view.findViewById(R.id.grilleAssos)
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
                adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)
                gridView.adapter = adapter
            } else {
                Log.d("MainActivity", "Aucune association trouvée")
            }
        }
        getAssosTask.execute()

        adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)
        gridView.adapter = adapter

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

        val btnFiltre = view.findViewById<Button>(R.id.assosFiltre)
        btnFiltre.setOnClickListener {
            showFilterDialog()
        }
        return view
    }

    private fun showFilterDialog() {
        val filters = getFiltersFromDatabase() // recup filtres
        val filterNames = filters.map { it?.name ?: "" }.toTypedArray() // nom des filtres
        val filterIds = filters.map { it?.id ?: "" }.toTypedArray() // id filtres

        val checkedItems = BooleanArray(filters.size)

        // boite de dialogue
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_filtre)

        builder.setMultiChoiceItems(filterNames, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }

        builder.setPositiveButton(R.string.confirm) { _, _ ->
            // recup les filtres
            val selectedFilters = filterIds.filterIndexed { index, _ -> checkedItems[index] }

            // applique les filtres
            if (selectedFilters.isNotEmpty()) {
                applyFilters(selectedFilters) // recup les assos
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun getFiltersFromDatabase(): List<Filter?> {
        return listOf(
            context?.let { Filter(1, it.getString(R.string.chroniques)) },
            context?.let { Filter(2, it.getString(R.string.Accidents)) },
            context?.let { Filter(3, it.getString(R.string.dépendance)) },
            context?.let { Filter(4, it.getString(R.string.mentale)) },
            context?.let { Filter(5, it.getString(R.string.Droits)) },
            context?.let { Filter(6, it.getString(R.string.Soutien)) },
            context?.let { Filter(7, it.getString(R.string.Prévention)) },
        )
    }

    data class Filter(val id: Int, val name: String)

    private fun applyFilters(selectedFilterIds: List<Any>) {
        val filtreString = selectedFilterIds.joinToString(",") { it.toString() }

        CoroutineScope(Dispatchers.Main).launch {
            val task = GetAssosByFilterTask()
            val associations = task.getAssosByFilterInBG(filtreString)

            updateAssociations(associations)
        }
    }

    private fun updateAssociations(associations: List<Assos>) {
        adapter = AssosAdapter(requireContext(), R.layout.item_asso, associations)
        gridView.adapter = adapter
    }
}