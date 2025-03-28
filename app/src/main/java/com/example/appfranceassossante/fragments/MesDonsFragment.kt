
package com.example.appfranceassossante.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.DonationAdapter
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetDonRecUserTask
import com.example.appfranceassossante.apiService.GetDonUniqueUserTask
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch

class MesDonsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private lateinit var tableMesDons: TableLayout
    private lateinit var tableMesDonsRec: TableLayout
    private var donations = mutableListOf<Don>()
    private var donationsRec = mutableListOf<Don>()
    private lateinit var viewLifecycleOwner: LifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_dons, container, false)


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

        return view
    }

    private fun loadDonationsData(view: View) {
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

        // header
        val rowHeader = TableRow(context).apply {
            addView(createHeaderTextView("Association"))
            addView(createHeaderTextView("Date"))
            addView(createHeaderTextView("Montant"))
            addView(createHeaderTextView("Paiement"))
        }
        tableMesDons.addView(rowHeader)

        val userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val mail = userViewModel.mail.value // recup le mail du user

        val getDonUnique = GetDonUniqueUserTask()
        donations = getDonUnique.getDonUniqueUserInBG(mail.toString()).toMutableList() // recup la liste de ses dons uniques

        // pour chaque don de la liste, crée une ligne et rentre les infos
        donations.forEach { don ->
            val row = TableRow(context).apply {
                addView(createDonTextView(don.association.toString()))
                addView(createDonTextView(don.date.toString()))
                addView(createDonTextView(don.montant.toString()))
                addView(createDonTextView(don.paiement))
            }
            tableMesDons.addView(row)
        }
    }

    // pareil que la méthode en haut mais pour les dons rec
    private suspend fun tableMesDonsRec() {
        val rowHeader = TableRow(context).apply {
            addView(createHeaderTextView("Association"))
            addView(createHeaderTextView("Date"))
            addView(createHeaderTextView("Montant"))
            addView(createHeaderTextView("Paiement"))
        }
        tableMesDonsRec.addView(rowHeader)

        val userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val mail = userViewModel.mail.value

        val getDonUniqueRec = GetDonRecUserTask()
        donationsRec = getDonUniqueRec.getDonRecUserInBG(mail.toString()).toMutableList()

        donationsRec.forEach { don ->
            val row = TableRow(context).apply {
                addView(createDonTextView(don.association.toString()))
                addView(createDonTextView(don.date.toString()))
                addView(createDonTextView(don.montant.toString()))
                addView(createDonTextView(don.paiement))
            }
            tableMesDonsRec.addView(row)
        }
    }

    // pour créer la ligne du header
    private fun createHeaderTextView(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setTypeface(typeface, Typeface.BOLD)
            setPadding(13, 0, 13, 5)
        }
    }

    // créer les lignes de don
    private fun createDonTextView(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setPadding(12,5,12,5)
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
                val sortedList = if (position == 0) {
                    donations.sortedBy { it.montant }
                } else {
                    donations.sortedByDescending { it.montant }
                }
                donationAdapter.updateData(ArrayList(sortedList))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun configureBackButton(view: View) {
        view.findViewById<Button>(R.id.btnretour).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}