package com.example.appfranceassossante.fragments.don

import CreateDonUTask
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.utilsTextSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.DonViewModel
import com.example.appfranceassossante.models.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DonTypeFragment : BaseFragment() {
    private lateinit var radioUnique: RadioButton
    private lateinit var radioRecurrent: RadioButton
    private lateinit var radioMensuel: RadioButton
    private lateinit var radioAnnuel: RadioButton
    private lateinit var dateFinEditText: EditText
    private lateinit var recurrentOptionsLayout: LinearLayout
    private lateinit var donViewModel: DonViewModel
    private lateinit var userViewModel: UserViewModel
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_don_type, container, false)

        radioUnique = view.findViewById(R.id.radioUnique)
        radioRecurrent = view.findViewById(R.id.radioRecurrent)
        radioMensuel = view.findViewById(R.id.radioMensuel)
        radioAnnuel = view.findViewById(R.id.radioAnnuel)
        dateFinEditText = view.findViewById(R.id.dateFinEditText)
        recurrentOptionsLayout = view.findViewById(R.id.recurrentOptionsLayout)
        donViewModel = ViewModelProvider(requireActivity()).get(DonViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        setRecurrentOptionsEnabled(false) //desactive les options recurrentes par defaut

        // Observer la date stockée et mettre à jour l'EditText
        donViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            date?.let {
                dateFinEditText.setText(dateFormat.format(it)) // Convertir Date en String
            }
        }

        radioRecurrent.setOnClickListener { // si on clique sur recurrent on active ses opt
            // Vérifier si l'utilisateur est connecté
            if (userViewModel.isUserLoggedIn()) {
                //activer les options récurrentes
                setRecurrentOptionsEnabled(true)
            } else {
                // Si l'utilisateur n'est pas connecté, afficher un message avec une boîte de dialogue
                showLoginDialog()
            }
        }

        radioUnique.setOnClickListener { // si on clique ca desactive les options recurrente
            setRecurrentOptionsEnabled(false)
            donViewModel.setSelectedDate(null)
        }

        dateFinEditText.setOnClickListener { //selectionner la date
            showDatePicker()
        }

        val suivant = view.findViewById<Button>(R.id.suivant)
        suivant.setOnClickListener{
            // Vérifier si le don est récurrent et si la date est renseignée
            if (radioRecurrent.isChecked && donViewModel.selectedDate.value == null) {
                // Si c'est un don récurrent mais qu'aucune date n'est sélectionnée
                dateFinEditText.error = "Veuillez entrer une date avant de continuer."}
            else {
                if(radioRecurrent.isChecked && radioMensuel.isChecked)
                    donViewModel.setMensuel(true)
                else
                    donViewModel.setMensuel(false)


                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                // remplace le fragment actuel par le fragment "DonPaiementFragment"
                transaction.replace(R.id.fragment_container, DonPaiementFragment())
                transaction.addToBackStack(null) // met le fragment actuel en backstack (pour y retourner rapidement en faisant retour)
                transaction.commit()
            }
        }

        val retour = view.findViewById<Button>(R.id.retour)
        retour.setOnClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            // remplace le fragment actuel par le fragment "DonMontantFragment"
            transaction.replace(R.id.fragment_container, DonMontantFragment())
            transaction.commit()
        }
        return view;
    }

    private fun setRecurrentOptionsEnabled(enabled: Boolean) {
        radioMensuel.isEnabled = enabled
        radioAnnuel.isEnabled = enabled
        dateFinEditText.isEnabled = enabled

        // Affiche ou cache les options de récurrence
        recurrentOptionsLayout.visibility = if (enabled) View.VISIBLE else View.GONE

        // Réinitialise la date si on repasse sur "Unique"
        if (!enabled) {
            dateFinEditText.setText("")
            donViewModel.setSelectedDate(null)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val date = selectedDate.time // Convertir Calendar en Date

                // Met à jour l'EditText et stocke la Date dans le ViewModel
                dateFinEditText.setText(dateFormat.format(date)) // Affichage formaté
                donViewModel.setSelectedDate(date) // Stockage dans ViewModel
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun showLoginDialog() {
        // Créer une boîte de dialogue pour inviter l'utilisateur à se connecter
        android.app.AlertDialog.Builder(requireContext())
            .setMessage("Vous devez être connecté pour effectuer un don récurrent. Voulez-vous vous connecter ?")
            .setPositiveButton("Se connecter") { _, _ ->
                // Naviguer vers le fragment de connexion
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, SeConnecterFragment()) // Remplacer par le fragment de connexion
                transaction.addToBackStack(null) // Ajouter dans le backstack
                transaction.commit()
            }
            .setNegativeButton("Annuler", null) // Annuler la boîte de dialogue
            .show()
    }



}