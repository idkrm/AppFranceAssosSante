package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosIDTask
import com.example.appfranceassossante.apiService.GetListYearDonRecTask
import com.example.appfranceassossante.apiService.GetListYearDonTask
import com.example.appfranceassossante.apiService.GetMonthDonTask
import com.example.appfranceassossante.apiService.GetTotalYearDonTask
import com.example.appfranceassossante.models.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class LesdonsFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var barChart: BarChart
    private lateinit var tableDon: TableLayout
    private lateinit var totalannee: Spinner
    private lateinit var totalrec: Spinner
    private var asId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_les_dons, container, false)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val nomassos = view.findViewById<TextView>(R.id.nomassociation)
        val nomAsso = userViewModel.admin.value?.getAssosName().toString()
        Log.d("Les dons fragment", "Le nom de l'assos: $nomAsso")

        nomassos.text = nomAsso

        GetAssosIDTask(nomAsso) { id ->
            Log.d("Les dons fragment", "Recherche l'id")
            if (id != null) {
                asId = id // L'ID récupéré est déjà une chaîne de caractères (String)
                Log.d("Les dons fragment", "L'asID: $asId")

                totalannee = view.findViewById(R.id.totalannee)
                GetListYearDonTask(asId!!) { yearsList ->
                    updateYearSpinner(totalannee, yearsList)
                }.execute()

                val montantannee = view.findViewById<TextView>(R.id.montantannee)
                val annee = view.findViewById<TextView>(R.id.annee)

                totalannee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Lorsque l'utilisateur sélectionne une année dans le spinner 'totalannee'
                        val selectedYear = totalannee.selectedItem.toString()
                        annee.text = selectedYear
                        // Exécuter la requête pour obtenir le total des dons de l'année sélectionnée
                        GetTotalYearDonTask(selectedYear, asId!!) { total ->
                            montantannee.text = "$total €"
                        }.execute()
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Aucune action nécessaire ici
                    }
                }

                totalrec = view.findViewById(R.id.totalrec)
                GetListYearDonRecTask(asId!!) { yearsList ->
                    updateYearSpinner(totalrec, yearsList)
                }.execute()
                val montantrec = view.findViewById<TextView>(R.id.montantanneedonation)
                val anneedonation = view.findViewById<TextView>(R.id.anneedonation)

                totalrec.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Lorsque l'utilisateur sélectionne une année dans le spinner 'totalannee'
                        val selectedYear = totalrec.selectedItem.toString()
                        anneedonation.text = selectedYear
                        // Exécuter la requête pour obtenir le total des dons de l'année sélectionnée
                        GetTotalYearDonTask(selectedYear, asId!!) { total ->
                            montantrec.text = "$total €"
                        }.execute()
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Aucune action nécessaire ici
                    }
                }

                barChart = view.findViewById(R.id.graph_bar)
                tableDon = view.findViewById(R.id.tabledon)

                remplirGraph()
                loadDonationData()
            } else {
                // Gérer le cas où l'ID est null (association non trouvée)
                println("Association non trouvée")
            }
        }.execute()
        nomassos.text = "123"
        return view
    }

    private fun updateYearSpinner(spinner: Spinner, yearsList: List<String>) {
        if (yearsList.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearsList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun loadDonationData() {
        // Récupère les années disponibles
        GetListYearDonRecTask(asId!!) { yearsList ->
            if (yearsList.isNotEmpty()) {
                val donationsData = mutableMapOf<String, MutableMap<String, String>>()
                var nbYear = yearsList.size
                for (year in yearsList) {
                    GetMonthDonTask(year, asId!!) { monthtotal ->

                        donationsData[year] = monthtotal.mapValues { (_, value) -> "$value €"}.toMutableMap()

                        // Vérifie si toutes les requêtes sont terminées
                        nbYear--
                        if (nbYear == 0) {
                            requireActivity().runOnUiThread {
                                addTableRows(tableDon, donationsData)
                            }
                        }
                    }.execute()
                }
            }
        }.execute()
    }

    private fun addTableRows(table: TableLayout, data: Map<String, Map<String, String>>){
        table.removeAllViews() // Nettoyer la table avant d'ajouter de nouvelles données

        // header
        val rowHeader = TableRow(table.context)
        val header = listOf("Année-Mois", getString(R.string.jan), getString(R.string.fev), getString(R.string.mars), getString(R.string.avr),
            getString(R.string.mai), getString(R.string.juin), getString(R.string.juil), getString(R.string.aout), getString(R.string.sept),
            getString(R.string.oct), getString(R.string.nov), getString(R.string.dec))

        for(h in header){
            val elem = TextView(table.context)
            elem.text = h
            elem.setPadding(8, 8, 8, 8)
            rowHeader.addView(elem)
        }

        table.addView(rowHeader)

        for ((year, values) in data) {
            val row = TableRow(table.context)

            // Ajouter l'année
            val annee = TextView(table.context)
            annee.text = year
            annee.setPadding(8, 8, 8, 8)
            row.addView(annee)

            // Ajouter les montants pour chaque mois
            for (value in values) {
                val montant = TextView(table.context)
                montant.text = value.value.toString()
                montant.setPadding(8, 8, 8, 8)
                row.addView(montant)
            }

            table.addView(row)
        }
    }

    private fun remplirGraph(){
        GetListYearDonTask(asId!!) { yearsList ->
            if (yearsList.isNotEmpty()) {
                val donationsData = mutableMapOf<String, Double>()
                var nbYear = yearsList.size
                for (year in yearsList) {
                    GetTotalYearDonTask(year, asId!!) { total ->

                        donationsData[year] = total

                        // Vérifie si toutes les requêtes sont terminées
                        nbYear--
                        if (nbYear == 0) {
                            requireActivity().runOnUiThread {
                                //addTableRows(tableDon, donationsData)
                                afficherBarChart(donationsData)
                            }
                        }
                    }.execute()
                }
            }
        }.execute()
    }

    private fun afficherBarChart(donationsData: Map<String, Double>) {
        val barEntries = ArrayList<BarEntry>() //liste contenant les valeurs du graphique
        val labels = ArrayList<String>() //liste contenant les années pour l'axe X

        var index = 0
        for ((year, montant) in donationsData.entries.sortedBy { it.key.toInt() }) {
            barEntries.add(BarEntry(index.toFloat(), montant.toFloat()))
            labels.add(year)
            index++
        }

        val barDataSet = BarDataSet(barEntries, "Dons annuels")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor = R.color.orange_splash
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.setFitBars(true)

        // Configurer l'axe X (les années)
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        // Configurer l'axe Y (les montants)
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // Animer le graphique
        barChart.animateY(1000)
        barChart.description.isEnabled = false
        barChart.invalidate() // Mettre à jour l'affichage
    }
}