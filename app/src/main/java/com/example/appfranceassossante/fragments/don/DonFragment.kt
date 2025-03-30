package com.example.appfranceassossante.fragments.don

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.example.appfranceassossante.R
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.apiService.GetAssosTask
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.models.DonViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class DonFragment : BaseFragment() {
    private lateinit var spinnerAssos: Spinner
    private var assosList: List<Assos> = listOf()
    private lateinit var donViewModel: DonViewModel
    private var nomAssos: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // recupere le nom de l'assos (passé depuis AssosInfoFragment) pour le spinner
        arguments?.let {
            nomAssos = it.getString("nom_assos")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate la vue avec le bon fragment
        val view = inflater.inflate(R.layout.fragment_don, container, false)
        spinnerAssos = view.findViewById(R.id.spinner_assos)
        donViewModel = ViewModelProvider(requireActivity()).get(DonViewModel::class.java)

        //spinner
        GetAssosTask { assos ->
            requireActivity().runOnUiThread { setupSpinner(assos) }
        }.execute()

        // recup le bouton suivant
        val btn = view.findViewById<Button>(R.id.suivant)
        btn.setOnClickListener{
            //on met assos dans donviewmodel
            val assoSelected = spinnerAssos.selectedItem.toString().trim()
            donViewModel.setAssociationName(assoSelected)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment qui suit ("DonMontantFragment")
            transaction.replace(R.id.fragment_container, DonMontantFragment())
            transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
            transaction.commit()
        }

        val assos = view.findViewById<TextView>(R.id.decouvrir)
        assos.setOnClickListener{
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_assoc // selectionne "assos"
        }
        return view;
    }


    private fun setupSpinner(assos: List<Assos>){
        assosList = assos // Stocke les Assos
        val nomsAssos = assos.map { it.getAssosName() } // Récupère juste le nom de l'assos

        // verifie si nomAssos est null ou pas
        val selectedAssociation = assosList.find { it.getAssosName() == nomAssos }
        selectedAssociation?.let {
            // Si l'association est trouvée, sélectionne cette association dans le spinner
            val position = assosList.indexOf(it)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_selectable_list_item, nomsAssos)
            adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item)
            spinnerAssos.adapter = adapter

            spinnerAssos.setSelection(position)
        }

        // Sinon, on sélectionne par défaut le premier élément du spinner
        if (nomsAssos.isNotEmpty() && selectedAssociation == null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_selectable_list_item, nomsAssos)
            adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item)
            spinnerAssos.adapter = adapter
            spinnerAssos.setSelection(0) // Sélectionne par défaut le premier élément
        }
    }
}