package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosIDTask
import com.example.appfranceassossante.apiService.GetDonsRecurrentsTask
import com.example.appfranceassossante.apiService.GetListYearDonRecTask
import com.example.appfranceassossante.apiService.GetListYearDonTask
import com.example.appfranceassossante.apiService.GetTotalYearDonTask
import com.example.appfranceassossante.models.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class LesdonsFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var barChart: BarChart
    private lateinit var tableDonMensuel: TableLayout
    private lateinit var tableDonAnnuel: TableLayout
    private lateinit var totalannee: Spinner
    private lateinit var anneeTab: Spinner
    private lateinit var moisTab: Spinner
    private var asId: String? = null
    private lateinit var selectedYear : String
    private lateinit var selectedMonth : String
    private lateinit var btnGraph: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_les_dons, container, false)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        barChart = view.findViewById(R.id.graph_bar)
        tableDonMensuel = view.findViewById(R.id.tabledonmensuel)
        tableDonAnnuel = view.findViewById(R.id.tabledonannuel)
        btnGraph = view.findViewById(R.id.btngraph)
        altText() // initialise le btn altText

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

                anneeTab = view.findViewById(R.id.annee_tab)
                GetListYearDonRecTask(asId!!) { yearsList ->
                    updateYearSpinner(anneeTab, yearsList)
                }.execute()

                moisTab = view.findViewById(R.id.mois_tab)
                val monthList = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
                updateMonthSpinner(moisTab, monthList)

                val anneetabmensuel = view.findViewById<TextView>(R.id.annee_tab_mensuel)
                val moistabmensuel = view.findViewById<TextView>(R.id.mois_tab_mensuel)
                val anneetabannuel = view.findViewById<TextView>(R.id.annee_tab_annuel)

                anneeTab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Lorsque l'utilisateur sélectionne une année dans le spinner 'totalannee'
                        selectedYear = anneeTab.selectedItem.toString()
                        anneetabmensuel.text = selectedYear
                        anneetabannuel.text = selectedYear
                        // Exécuter la requête pour obtenir le total des dons de l'année sélectionnée
                        loadDonationDataIfReady()
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Aucune action nécessaire ici
                    }
                }
                moisTab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Lorsque l'utilisateur sélectionne une année dans le spinner 'totalannee'
                        selectedMonth = moisTab.selectedItem.toString()
                        moistabmensuel.text = selectedMonth
                        loadDonationDataIfReady()
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Aucune action nécessaire ici
                    }
                }

                remplirGraph()
            } else {
                // Gérer le cas où l'ID est null (association non trouvée)
                println("Association non trouvée")
            }
        }.execute()
        return view
    }

    private fun updateYearSpinner(spinner: Spinner, yearsList: List<String>) {
        if (yearsList.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearsList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun updateMonthSpinner(spinner: Spinner, monthList: List<String>) {
        if (monthList.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun loadDonationDataIfReady() {
        // Vérifier que l'année et le mois sont sélectionnés
        if (::selectedYear.isInitialized && ::selectedMonth.isInitialized) {
            loadDonationDataMensuel()  // Charge les données mensuelles
            loadDonationDataAnnuel()   // Charge les données annuelles
        }
    }

    private fun loadDonationDataMensuel() {

        GetDonsRecurrentsTask(
            year = selectedYear,
            month = selectedMonth,
            assosID = asId!!,
            onSuccess = { dons ->
                val contenuMensuel = listOf(dons.countMensuel.toString(), dons.totalMensuel.toString() + " €")
                Log.d("LesdonsFragment", contenuMensuel.toString())
                addTableRows(tableDonMensuel, contenuMensuel)
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        ).execute()
    }

    private fun loadDonationDataAnnuel() {
        GetDonsRecurrentsTask(
            year = selectedYear,
            month = selectedMonth,
            assosID = asId!!,
            onSuccess = { dons ->
                val contenuAnnuel = listOf(dons.countAnnuel.toString(), dons.totalAnnuel.toString() + " €")
                Log.d("LesdonsFragment", contenuAnnuel.toString())
                addTableRows(tableDonAnnuel, contenuAnnuel)
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        ).execute()
    }

    private fun addTableRows(table: TableLayout, data: List<String>){
        table.removeAllViews() // Nettoyer la table avant d'ajouter de nouvelles données

        // header
        val rowHeader = TableRow(requireContext()).apply {
            gravity = Gravity.CENTER // Centrer toute la ligne
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            listOf("Nombre de dons", "Somme des dons").forEach { headerText ->
                val textView = TextView(requireContext()).apply {
                    text = headerText
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER
                }
                addView(textView,  TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
            }
        }
        table.addView(rowHeader)

        val contenu = TableRow(requireContext()).apply {
            gravity = Gravity.CENTER // Centrer toute la ligne
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            data.forEach { value ->
                val textView = TextView(requireContext()).apply {
                    text = value
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER // Centrer le texte dans la cellule
                }
                addView(textView, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
            }
        }
        table.addView(contenu)
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
        val customColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.fairedon2),
            ContextCompat.getColor(requireContext(), R.color.purple_200),
            ContextCompat.getColor(requireContext(), R.color.turquoise_splash),
            ContextCompat.getColor(requireContext(), R.color.roseclair),
            ContextCompat.getColor(requireContext(), R.color.violet_splash),
            ContextCompat.getColor(requireContext(), R.color.turquoise_logo),
            ContextCompat.getColor(requireContext(), R.color.bleu_logo),
        )

        //barDataSet.colors = ColorTemplate.PASTEL_COLORS.toList()
        barDataSet.colors = customColors
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

    private fun altText() {
        btnGraph.setOnClickListener {
            // Créer un message contenant les informations du graphique
            val donationsData = mutableMapOf<String, Double>() // Simuler les données des dons annuels
            val message = StringBuilder()

            // Récupérer les données des dons annuels (par année)
            GetListYearDonTask(asId!!) { yearsList ->
                var nbYear = yearsList.size
                for (year in yearsList) {
                    // Récupérer le total des dons pour chaque année
                    GetTotalYearDonTask(year, asId!!) { total ->

                        donationsData[year] = total

                        // Vérifie si toutes les requêtes sont terminées
                        nbYear--
                        if (nbYear == 0) {
                            // Générer le message à afficher
                            message.append("Graphiques en barres illustrant le montant total des dons reçus pour chaque année :\n\n")
                            for ((year, amount) in donationsData.entries.sortedBy { it.key.toInt() }) {
                                message.append("$year : ${amount} €\n")
                            }

                            // Afficher la boîte de dialogue avec les données
                            android.app.AlertDialog.Builder(requireContext())
                                .setTitle("Texte alternative")
                                .setMessage(message.toString()) // Le message contenant les données
                                .setPositiveButton("OK", null) // Bouton OK pour fermer la boîte de dialogue
                                .show()
                        }
                    }.execute()
                }
            }.execute()
        }
    }
}