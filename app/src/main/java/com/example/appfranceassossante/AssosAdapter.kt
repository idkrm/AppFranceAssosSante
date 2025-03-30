package com.example.appfranceassossante

import android.widget.ArrayAdapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appfranceassossante.fragments.AssosInfoFragment
import com.example.appfranceassossante.models.Assos

class AssosAdapter(private val activity: Context,
                   private val itemResource: Int,
                   private var assos: List<Assos>
) : ArrayAdapter<Assos>(activity, itemResource, assos ), Filterable {
    override fun getCount(): Int {
        return assos.size
    }

    override fun getItem(position: Int): Assos? {
        return assos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = convertView ?: LayoutInflater.from(activity).inflate(itemResource, parent, false) // ?: est l'opérateur Elvis comme un if
        val asso = assos[position]

        val assosName : TextView = layout.findViewById(R.id.assos_name)
        val assosLogo : ImageView = layout.findViewById(R.id.assos_image)

        assosName.text = asso.getAcronyme()

        Glide.with(context)
            .load(asso.getImg()) // Votre URL
            .placeholder(R.drawable.placeholder_asso) // Image en attente
            .into(assosLogo)
        //assosLogo.setImageResource(asso.getImg())

        // recup le nom de l'assos pour fragment_assos_info
        layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nom_assos", asso.getAssosName())

            val fragment = AssosInfoFragment()
            fragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return layout
    }

}