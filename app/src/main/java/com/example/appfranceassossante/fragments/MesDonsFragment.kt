
package com.example.appfranceassossante.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfranceassossante.models.Don
import com.example.appfranceassossante.DonationAdapter
import com.example.appfranceassossante.R
//import com.example.appfranceassossante.mongodb.MongoDBConnection

class MesDonsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var donationAdapter: DonationAdapter
    private val donations = mutableListOf<Don>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_dons, container, false)

        //dons
        recyclerView = view.findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email", null)

        if (email != null) {
            //val mongoDbConnection = MongoDBConnection()
            //val userDonations = mongoDbConnection.getUserDonations(email)
            //donations.addAll(userDonations)

            donationAdapter = DonationAdapter(donations)
            recyclerView.adapter = donationAdapter
        }

        //filtre des dons
        val spinner = view.findViewById<Spinner>(R.id.histodon)
        val filtre = arrayOf("Croissant", "Décroissant")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filtre)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val btn = view.findViewById<Button>(R.id.btnretour)
        btn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        //dons recurrents

        // Inflate the layout for this fragment
        return view
    }


}