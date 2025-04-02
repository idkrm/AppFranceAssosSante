
package com.example.appfranceassossante.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.os.Build
import java.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.DonationAdapter
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.DeleteDonRTask
import com.example.appfranceassossante.apiService.GetDonRecUserTask
import com.example.appfranceassossante.apiService.GetDonUniqueUserTask
import com.example.appfranceassossante.models.DonRecurrent
import com.example.appfranceassossante.models.UserViewModel
import io.realm.internal.Table
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MesDonsFragment : BaseFragment() {
    //private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private lateinit var mail: String
    private lateinit var donationAdapter: DonationAdapter
    private lateinit var tableMesDons: TableLayout
    private lateinit var tableMesDonsRec: TableLayout
    private var donations = mutableListOf<Don>()
    private var donationsRec = mutableListOf<DonRecurrent>()
    private var selectedDon: DonRecurrent? = null
    private lateinit var annulerButton: Button
    private lateinit var btnInfo: Button
    private var rowBefore: TableRow? = null
    //private lateinit var viewLifecycleOwner: LifecycleOwner

    private val columnWeights = floatArrayOf(3.5f, 2.3f, 2.5f, 3f)
    private val columnWeightsRec = floatArrayOf(3.5f, 3f, 2.5f, 2.3f)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_dons, container, false)
        annulerButton = view.findViewById(R.id.annule)
        btnInfo = view.findViewById(R.id.info)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        mail = userViewModel.mail.value.toString() // recup le mail du user
        Log.d("MesDonsFragment", "Mail utilisateur récupéré: $mail")

        //dons
//        recyclerView.layoutManager = LinearLayoutManager(context)
        donationAdapter = DonationAdapter(donations)
//        recyclerView.adapter = donationAdapter
//
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email", null)
//
//        if (email != null) {
//            GetDonationsTask(requireContext()).execute(email) { donations ->
//                if (donations != null) {
//                    donationAdapter.updateData(donations)
//                    Log.d("DonationsActivity", "Dons chargés : ${donations.size}")
//                } else {
//                    Log.e("DonationsActivity", "Échec du chargement des dons")
//                }
//            }
//        }

        tableMesDons = view.findViewById(R.id.table_mes_dons)
        tableMesDonsRec = view.findViewById(R.id.table_mes_dons_rec)

        loadDonationsData(view) // charge les dons

        configureSpinner(view) // charge le spinner
        configureBackButton(view) // le btn back

        setupAnnulerButton()
        setupBtnInfo()

        return view
    }

    private fun loadDonationsData(view: View) {
        Log.d("MesDonsFragment", "Chargement des données des dons")
        // lifecyclescope pour gérer coroutines (jsp c quoi)
        lifecycleScope.launch {
            try {
                tableMesDons() // charge les dons uniques
                tableMesDonsRec() // charge les dons récurrents
            } catch (e: Exception) {
                Log.e("MesDonsFragment", "Erreur lors du chargement des dons", e)
            }
        }
    }

    private suspend fun tableMesDons() {
        tableMesDons.removeAllViews()

        // Définition d'un poids total pour la ligne
        tableMesDons.weightSum = columnWeights.sum()

        // Création du header
        val rowHeader = TableRow(context).apply {
            columnWeights.forEachIndexed { index, weight ->
                addView(createHeaderTextView(listOf("Association", "Date", "Montant", "Paiement")[index], weight))
            }
        }
        tableMesDons.addView(rowHeader)

        // Récupération des données
        val getDonUnique = GetDonUniqueUserTask()
        donations = getDonUnique.getDonUniqueUserInBG(mail).toMutableList()
        Log.d("MesDonsFragment", "Nombre de dons uniques récupérés: ${donations.size}")

        // Ajout des données à la table
        donations.forEach { don ->
            val row = TableRow(context).apply {
                addView(createDonTextView(don.association, columnWeights[0]))
                addView(createDonTextView(formatDate(don.date.toString()), columnWeights[1])) // Formatage de la date
                addView(createDonTextView(formatMontant(don.montant), columnWeights[2])) // Formatage du montant
                addView(createDonTextView(don.paiement, columnWeights[3]))
            }
            tableMesDons.addView(row)
        }
    }

    // pareil que la méthode en haut mais pour les dons rec
    private suspend fun tableMesDonsRec() {
        tableMesDonsRec.removeAllViews()

        // Définition d'un poids total pour la ligne
        tableMesDonsRec.weightSum = columnWeightsRec.sum()

        // Création du header
        val rowHeader = TableRow(context).apply {
            columnWeightsRec.forEachIndexed { index, weight ->
                addView(createHeaderTextView(listOf("Association", "Prochain\ndébit", "Montant", "Type")[index], weight))
            }
        }
        tableMesDonsRec.addView(rowHeader)

        val getDonUniqueRec = GetDonRecUserTask()
        donationsRec = getDonUniqueRec.getDonRecUserInBG(mail).toMutableList()
        Log.d("MesDonsFragment", "Nombre de dons récurrents récupérés: ${donationsRec.size}")

        donationsRec.forEach { don ->
            val adjustedDate = Calendar.getInstance().apply {
                when (don.frequence.lowercase()) {
                    "Mensuel" -> {
                        set(Calendar.DAY_OF_MONTH, 1) // On se place au début du mois
                        add(Calendar.MONTH, 1) // Ajoute 1 mois à partir de maintenant
                    }
                    "Annuel" -> {
                        set(Calendar.MONTH, Calendar.JANUARY) // On se place au début de l'année
                        set(Calendar.DAY_OF_MONTH, 1)
                        add(Calendar.YEAR, 1) // Ajoute 1 an
                    }
                }
            }.time

            val row = TableRow(context).apply {
                addView(createDonTextView(don.association, columnWeightsRec[0]))
                addView(createDonTextView(formatDate(adjustedDate.toString()), columnWeightsRec[1])) // Affiche la nouvelle date
                addView(createDonTextView(formatMontant(don.montant), columnWeightsRec[2])) // Formatage du montant
                addView(createDonTextView(don.frequence, columnWeightsRec[3]))

                setOnClickListener {
                    toggleSelection(don)
                }
            }
            tableMesDonsRec.addView(row)
        }
    }
    // Fonction qui gère la sélection et la désélection d'une ligne
    private fun toggleSelection(don: DonRecurrent) {
        val rowIndex = donationsRec.indexOf(don) + 1 // Trouve l'index du don dans la liste

        if (rowIndex != -1) {
            val row = tableMesDonsRec.getChildAt(rowIndex) as TableRow // Récupère la ligne associée

            if (selectedDon == don) {
                //désélectionner le don
                selectedDon = null
                view?.findViewById<Button>(R.id.annule)?.setBackgroundResource(R.drawable.btn_faire_don3)
                view?.findViewById<Button>(R.id.info)?.setBackgroundResource(R.drawable.btn_faire_don3)
                // Remettre la couleur initiale
                row.setBackgroundColor(resources.getColor(R.color.fond_bleu, null))
                rowBefore = null
            } else {
                // Désélectionner la ligne précédente si elle existe
                rowBefore?.setBackgroundColor(resources.getColor(R.color.fond_bleu, null))
                // Sélectionner le nouveau don
                selectedDon = don
                view?.findViewById<Button>(R.id.annule)?.setBackgroundResource(R.drawable.btn_faire_don2)
                view?.findViewById<Button>(R.id.info)?.setBackgroundResource(R.drawable.btn_faire_don2)
                row.setBackgroundResource(R.drawable.btn_suivant)
                rowBefore = row
            }
        }


}

    // pour créer la ligne du header
    private fun createHeaderTextView(text: String, weight: Float): TextView {
        return TextView(context).apply {
            this.text = text
            setTypeface(typeface, Typeface.BOLD)
            setPadding(13, 8, 13, 8)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)
        }
    }

    // créer les lignes de don
    private fun createDonTextView(text: String, weight: Float): TextView  {
        return TextView(context).apply {
            this.text = text
            setPadding(13, 10, 13, 10)
            gravity = Gravity.CENTER
            textSize = 15f
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)
        }
    }

    // configure le filtre
    private fun configureSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.histodon)
        val filtre = arrayOf("Croissant", "Décroissant")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filtre)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ascending = position == 0 // Croissant si 0, sinon décroissant

                // Trier les listes existantes
                donations.sortWith(compareBy { it.montant })
                donationsRec.sortWith(compareBy { it.montant })

                if (!ascending) {
                    donations.reverse()
                    donationsRec.reverse()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private suspend fun updateDonationsTable() {
        tableMesDons.removeAllViews()

        // Définition d'un poids total pour la ligne
        tableMesDons.weightSum = columnWeights.sum()

        // Création du header
        val rowHeader = TableRow(context).apply {
            columnWeights.forEachIndexed { index, weight ->
                addView(createHeaderTextView(listOf("Association", "Date", "Montant", "Paiement")[index], weight))
            }
        }
        tableMesDons.addView(rowHeader)

        // Récupération des données
        val getDonUnique = GetDonUniqueUserTask()
        donations = getDonUnique.getDonUniqueUserInBG(mail).toMutableList()
        Log.d("MesDonsFragment", "Nombre de dons uniques récupérés: ${donations.size}")

        // Ajout des données à la table
        donations.forEach { don ->
            val row = TableRow(context).apply {
                addView(createDonTextView(don.association, columnWeights[0]))
                addView(createDonTextView(formatDate(don.date.toString()), columnWeights[1])) // Formatage de la date
                addView(createDonTextView(formatMontant(don.montant), columnWeights[2])) // Formatage du montant
                addView(createDonTextView(don.paiement, columnWeights[3]))

            }
            tableMesDons.addView(row)
        }
    }

    private fun configureBackButton(view: View) {
        view.findViewById<Button>(R.id.btnretour).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Gérer le fuseau horaire si besoin
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date ?: Date()) // Si erreur, retourne la date actuelle
        } catch (e: Exception) {
            Log.e("MesDonsFragment", "Erreur de formatage de la date: $inputDate", e)
            inputDate // Retourne la date brute en cas d'échec
        }
    }

    private fun formatMontant(amount: Double): String {
        return String.format(Locale.FRANCE, "%.0f€", amount) // Supprime la décimale et ajoute l'€
    }

    // Bouton annuler pour supprimer un don après confirmation
    private fun setupAnnulerButton() {

        annulerButton?.setOnClickListener {
            // Si un don est sélectionné, on affiche la boîte de dialogue de confirmation
            if (selectedDon != null) {
                showConfirmationDialog(selectedDon!!)
            } else {
                Toast.makeText(requireContext(), "Aucun don sélectionné", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showConfirmationDialog(don: DonRecurrent) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Êtes-vous sûr de vouloir supprimer ce don récurrent ?")
            .setPositiveButton("Supprimer") { _, _ ->
                // Supprimer le don
                lifecycleScope.launch {
                    onDeleteDonClick(don)
                }
            }
            .setNegativeButton("Annuler", null)
            .create()

        dialog.show()
    }


    private suspend fun onDeleteDonClick( don: DonRecurrent) {
        // Récupère les données de la ligne sélectionnée
        val email = userViewModel.mail.value as String
        val assos = don.association
        val frequence = don.frequence

        // Appel à la méthode deleteDon pour supprimer le don récurrent
        deleteDon(email, assos, frequence)

        // Met à jour le tableau après suppression
        updateDonationsTable()
    }

    private fun deleteDon(email: String, assos: String, frequence:String){
        val deleteTask = DeleteDonRTask(requireContext())
        deleteTask.execute(email, frequence, assos) { success ->
            if (success) {
                Log.d("MainActivity", "Le don récurrent a été annulé.")
            } else {
                // Faire quelque chose en cas d'échec
                Log.e("MainActivity", "L'annulation du don a échoué.")
            }
        }
    }

    private fun setupBtnInfo(){
        btnInfo.setOnClickListener {
            // Vérifier si un don a été sélectionné
            val don = selectedDon
            if (don != null) {
                // Créer une boîte de dialogue pour afficher les informations du don
                val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Informations sur le don")
                    .setMessage("Association : ${don.association}\n" +
                            "Montant : ${formatMontant(don.montant)}\n" +
                            "Fréquence : ${don.frequence}\n" +
                            "Date de début : ${formatDate(don.date.toString())}\n" +
                            "Date de fin : ${formatDate(don.dateFin.toString())}")
                    .setPositiveButton("OK", null)  // Le bouton "OK" pour fermer la boîte de dialogue
                    .create()

                dialog.show()  // Affiche la boîte de dialogue
            } else {
                // Si aucun don n'est sélectionné, afficher un message d'erreur
                Toast.makeText(requireContext(), "Aucun don sélectionné", Toast.LENGTH_SHORT).show()
            }
        }
    }
}