package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import com.github.mikephil.charting.charts.BarChart

class LesdonsFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var barChart: BarChart
    private lateinit var tableDon: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_les_dons, container, false)

        val nomassos = view.findViewById<TextView>(R.id.nomassociation)
        nomassos.text = userViewModel.admin.value?.getAssosName().toString()

        val totalannee = view.findViewById<Spinner>(R.id.totalannee)
        val totalrec = view.findViewById<Spinner>(R.id.totalrec)

        barChart = view.findViewById(R.id.graph_bar)
        tableDon = view.findViewById<TableLayout>(R.id.tabledon)

        // Simuler les données récupérées depuis la base (remplacez cela par une vraie requête)
        val data = mapOf(
            "2025" to listOf("100€", "120€", "90€"),
            "2024" to listOf("80€", "110€", "75€"),
            "2023" to listOf("95€", "105€", "85€")
        )

        addTableRows(tableDon, data)

        return view
    }

    private fun fragmentRemplace(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("fragment")
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

    private fun addTableRows(table: TableLayout, data: Map<String, List<String>>){
        val annee = listOf("2025", "2024", "2023") //a recup via base de donnée

        for (i in annee.indices) {
            val row = TableRow(table.context)

            // Ajouter l'année
            val anneeTextView = TextView(table.context)
            anneeTextView.text = annee[i]
            anneeTextView.setPadding(8, 8, 8, 8)
            row.addView(anneeTextView)

            // Ajouter les montants pour chaque mois
            for ((_, values) in data) {
                val montantTextView = TextView(table.context)
                montantTextView.text = if (i < values.size) values[i] else "N/A"
                montantTextView.setPadding(8, 8, 8, 8)
                row.addView(montantTextView)
            }

            table.addView(row)
        }
    }
}