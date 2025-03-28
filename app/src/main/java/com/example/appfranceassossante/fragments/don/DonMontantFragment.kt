package com.example.appfranceassossante.fragments.don

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.DonViewModel

class DonMontantFragment : Fragment() {
    private lateinit var donViewModel: DonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_montant, container, false)

        val montantEditText = view.findViewById<EditText>(R.id.montant_rentrer)
        val buttonMinus = view.findViewById<Button>(R.id.buttonMinus)
        val buttonPlus = view.findViewById<Button>(R.id.buttonPlus)
        donViewModel = ViewModelProvider(requireActivity()).get(DonViewModel::class.java)

        montantEditText.setText("10") // 10euros par defaut
        montantEditText.inputType = InputType.TYPE_CLASS_NUMBER //que des nombres
        //montant superieur à 1euro
        montantEditText.filters = arrayOf(InputFilter { source, _, _, dest, _, _ ->
            val newValue = (dest.toString() + source.toString()).toIntOrNull()
            if (newValue != null && newValue >= 1) source else ""
        })
        //quand on clique sur + ca augmente de 10euros
        buttonPlus.setOnClickListener {
            val currentValue = montantEditText.text.toString().toIntOrNull() ?: 10
            montantEditText.setText((currentValue + 10).toString())
        }
        //quand on clique sur - ca diminue de 10
        buttonMinus.setOnClickListener {
            val currentValue = montantEditText.text.toString().toIntOrNull() ?: 10
            val newValue : Int
            if(currentValue < 10)
                newValue = (currentValue - 1).coerceAtLeast(1)
            else
                newValue = (currentValue - 10).coerceAtLeast(1) // Empêche d'aller en dessous de 1
            montantEditText.setText(newValue.toString())
        }

        val suivant = view.findViewById<Button>(R.id.suivant)
        suivant.setOnClickListener{
            //stocke le montant dans DonViewModel
            val value = montantEditText.text.toString().toIntOrNull() ?: 10
            donViewModel.setMontant(value)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonTypeFragment"
            transaction.replace(R.id.fragment_container, DonTypeFragment())
            transaction.addToBackStack(null) // met le fragment actuel en backstack (pour y retourner rapidement en faisant retour)
            transaction.commit()
        }

        val retour = view.findViewById<Button>(R.id.retour)
        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonFragment"
            transaction.replace(R.id.fragment_container, DonFragment())
            transaction.commit()
        }
        return view;
    }
}