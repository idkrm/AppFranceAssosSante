package com.example.appfranceassossante.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appfranceassossante.utilsTextSize.BaseFragment
import com.example.appfranceassossante.R
import com.example.appfranceassossante.models.UserViewModel
import java.util.Locale

class ProfilAdminFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil_admin, container, false)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val civ = view.findViewById<TextView>(R.id.civilitepersonne)
        civ.text = userViewModel.civilite.toString()

        val nom = view.findViewById<TextView>(R.id.nompersonne)
        nom.text = userViewModel.nom.toString().uppercase()

        val prenom = view.findViewById<TextView>(R.id.prenompersonne)
        prenom.text = userViewModel.prenom.toString() // mets la première lettre en maj
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        val mail = view.findViewById<TextView>(R.id.mailpersonne)
        mail.text = userViewModel.mail.toString()

        val assos = view.findViewById<TextView>(R.id.assospersonne)
        assos.text = userViewModel.admin.value?.getAssosName().toString()

        val btnlangue = view.findViewById<Button>(R.id.langue)
        btnlangue.setOnClickListener{
            fragmentRemplace(LangueFragment()) // remplace le fragment actuel par le fragment qui suit ("LangueFragment")
        }

        val btndons = view.findViewById<Button>(R.id.don)
        btndons.setOnClickListener{
            fragmentRemplace(LesdonsFragment()) // remplace le fragment actuel par le fragment qui suit ("LesdonsFragment")
        }

        val btndeco = view.findViewById<Button>(R.id.btn_deco)
        btndeco.setOnClickListener{
            userViewModel.reinitialiserDonnees()
            userViewModel.setUserLoggedIn(false)
            fragmentRemplace(SeConnecterFragment()) // remplace le fragment actuel par le fragment qui suit ("SeConnecterFragment")
        }

        val btnlegacy = view.findViewById<Button>(R.id.legacy)
        btnlegacy.setOnClickListener{

        }

        val btnpolicy = view.findViewById<Button>(R.id.policy)
        btnpolicy.setOnClickListener{

        }

        val btncondition = view.findViewById<Button>(R.id.condition)
        btncondition.setOnClickListener{

        }

        return view
    }

    private fun fragmentRemplace(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // remplace le fragment actuel par le fragment qui suit ("fragment")
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // ajoute le fragment actuel au backstack (pour pouvoir retourner dessus quand on fait retour sur le tel)
        transaction.commit()
    }

}