package com.example.appfranceassossante

import android.widget.ArrayAdapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

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
        assosLogo.setImageResource(asso.getAssosLogo())

        return layout
    }

}