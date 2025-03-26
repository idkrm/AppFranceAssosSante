package com.example.appfranceassossante

import android.widget.ArrayAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.appfranceassossante.models.Assos

class AssosAdapter(private val activity: Context,
                   private val itemResource: Int,
                   private val assos : List<Assos>
) : ArrayAdapter<Assos?>(activity, itemResource, assos) {

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layout = convertView ?: LayoutInflater.from(activity).inflate(itemResource, parent, false) // ?: est l'opérateur Elvis comme un if
        val asso = assos[position]

        val assosName : TextView = layout.findViewById(R.id.assos_name)
        val assosLogo : ImageView = layout.findViewById(R.id.assos_image)

        assosName.setText(asso.getAssosName())
        Glide.with(context)
            .load(asso.getImg()) // Votre URL
            .placeholder(R.drawable.placeholder_asso) // Image en attente
            .into(assosLogo)
        //assosLogo.setImageResource(asso.getImg())

        return layout
    }

}