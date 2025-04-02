package com.example.appfranceassossante.fragments.don

import CreateDonRTask
import CreateDonUTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.AccueilFragment
import com.example.appfranceassossante.models.DonViewModel
import com.example.appfranceassossante.models.UserViewModel
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class DonPaiementFragment : BaseFragment() {
    private lateinit var donViewModel: DonViewModel
    private lateinit var radioGroup: RadioGroup
    private lateinit var createDonUTask: CreateDonUTask
    private lateinit var userViewModel: UserViewModel
    private lateinit var createDonRTask: CreateDonRTask

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_paiement, container, false)
        donViewModel = ViewModelProvider(requireActivity()).get(DonViewModel::class.java)
        radioGroup = view.findViewById(R.id.radioTypeDon)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        createDonUTask = CreateDonUTask(requireContext())
        createDonRTask = CreateDonRTask(requireContext())

        val payer = view.findViewById<Button>(R.id.payer)
        val retour = view.findViewById<Button>(R.id.retour)

        payer.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId == -1) {
                Toast.makeText(requireContext(), getString(R.string.modepaiement), Toast.LENGTH_LONG).show()
                return@setOnClickListener // Arrête l'exécution ici
            }/////

            // Récupérer le RadioButton sélectionné
            val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)

            val selectedPaymentType = selectedRadioButton.text.toString()

            // Stocker le type de paiement dans le DonViewModel
            donViewModel.setPaymentType(selectedPaymentType)
            //utilisateur
            if(userViewModel.isUserLoggedIn()) {
                donViewModel.setUtilisateurEmail(userViewModel.mail.value)
                donViewModel.setUtilisateurEmailRec(userViewModel.mail.value as String)
            }

            //recupere l'assos a laquelle on a fait un don
            val associationName = donViewModel.getAssociationName()
            saveDonToDatabase()
            Toast.makeText(requireContext(), getString(R.string.donok), Toast.LENGTH_LONG).show()

            // Créer et afficher un AlertDialog avec un bouton pour retourner à l'AccueilFragment
            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setMessage(associationName + getString(R.string.remerciement))
                .setPositiveButton(getString(R.string.retouraccueil)) { _, _ ->
                    // Naviguer vers l'AccueilFragment lorsque l'utilisateur clique sur le bouton
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, AccueilFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()

                    // selectionne l'item accueil dans le menu
                    val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                    bottomNav?.selectedItemId = R.id.navigation_accueil
                }
                .create()

            dialog.show()

        }

        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonTypeFragment"
            transaction.replace(R.id.fragment_container, DonTypeFragment())
            transaction.commit()
        }
        return view;
    }
    private fun saveDonToDatabase() {


        lifecycleScope.launch {
            try {
                val donCreated : Boolean
                if(donViewModel.isUnique()){
                    val donData = donViewModel.collectDonUData()
                    donCreated = createDonUTask.createDonUInBG(donData)
                    Log.d("DON_DEBUG", "Données du don unique : $donData")////
                }

                else{
                    val donData = donViewModel.collectDonRData()
                    donCreated = createDonRTask.createDonRInBG(donData)}
                //reinitialisation
                donViewModel.reinitialiserDonnees()
                if (donCreated) {
                    Log.d("DON","Don créer")
                } else {
                    Log.d("DON","Erreur lors de la création du don")
                }
            } catch (e: Exception) {
                Log.d("DON","Erreur serveur")
            }
        }
    }
}