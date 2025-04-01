package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.text.font.Typeface
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosIDTask
import com.example.appfranceassossante.apiService.GetListDonsRecByYearTask
import com.example.appfranceassossante.apiService.GetListYearDonRecTask
import com.example.appfranceassossante.apiService.GetListYearDonTask
import com.example.appfranceassossante.apiService.GetTotalYearDonRecTask
import com.example.appfranceassossante.apiService.GetTotalYearDonTask
import com.example.appfranceassossante.models.DonRecurrent
import com.example.appfranceassossante.models.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Locale

class LesdonsFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var barChart: BarChart
    private lateinit var tableDonMensuel: TableLayout
    private lateinit var tableDonAnnuel: TableLayout
    private lateinit var totalannee: Spinner
    private lateinit var totalrec: Spinner
    private var asId: String? = null
    private lateinit var selectedYear : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_les_dons, container, false)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        barChart = view.findViewById(R.id.graph_bar)
        tableDonMensuel = view.findViewById(R.id.tabledonmensuel)
        tableDonAnnuel = view.findViewById(R.id.tabledonannuel)

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
                        selectedYear = totalrec.selectedItem.toString()
                        anneedonation.text = selectedYear
                        // Exécuter la requête pour obtenir le total des dons de l'année sélectionnée
                        GetTotalYearDonRecTask(selectedYear, asId!!) { total_rec ->
                            montantrec.text = "$total_rec €"
                        }.execute()
                        loadDonationData()
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

    private fun loadDonationData() {
        GetListDonsRecByYearTask(
            year = selectedYear,
            assosID = asId!!,
            onSuccess = { donations ->
                addTableRows(tableDonMensuel, donations)
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        ).execute()
    }

    private fun addTableRows(table: TableLayout, data: List<DonRecurrent>){
        table.removeAllViews() // Nettoyer la table avant d'ajouter de nouvelles données

        // header
        val rowHeader = TableRow(requireContext()).apply {
            listOf("Nombre de dons", "Somme des dons").forEach { headerText ->
                val textView = TextView(requireContext()).apply {
                    text = headerText
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER
                }
                addView(textView)
            }
        }
        table.addView(rowHeader)

        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        data.forEach { don ->
            TableRow(requireContext()).apply {
                listOf(
                    don.emailUtilisateur,
                    "${don.montant} €",
                    don.frequence,
                    dateFormat.format(don.date),
                    dateFormat.format(don.dateFin)
                ).forEach { value ->
                    val textView = TextView(requireContext()).apply {
                        text = value
                        setPadding(8, 8, 8, 8)
                    }
                    addView(textView)
                }
                table.addView(this)
            }
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
}