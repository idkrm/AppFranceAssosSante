package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.DonViewModel

class DonPaiementFragment : Fragment() {
    private lateinit var donViewModel: DonViewModel
    private lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_paiement, container, false)
        donViewModel = ViewModelProvider(requireActivity()).get(DonViewModel::class.java)
        radioGroup = view.findViewById(R.id.radioTypeDon)

        val payer = view.findViewById<Button>(R.id.payer)
        val retour = view.findViewById<Button>(R.id.retour)

        payer.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            // Récupérer le RadioButton sélectionné
            val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
            val selectedPaymentType = selectedRadioButton.text.toString()

            // Stocker le type de paiement dans le DonViewModel
            donViewModel.setPaymentType(selectedPaymentType)
            //recupere l'assos a laquelle on a fait un don
            val associationName = donViewModel.getAssociationName()
            Toast.makeText(requireContext(), "Vous avez fait un don !", Toast.LENGTH_LONG).show()

            // Créer et afficher un AlertDialog avec un bouton pour retourner à l'AccueilFragment
            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setMessage("$associationName vous remercie pour votre don !")
                .setPositiveButton("Retour à l'Accueil") { _, _ ->
                    // Naviguer vers l'AccueilFragment lorsque l'utilisateur clique sur le bouton
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, AccueilFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
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
}