package com.example.appfranceassossante

import android.widget.ArrayAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.appfranceassossante.models.Assos

class AssosAdapter(private val activity: Context,
                   private val itemResource: Int,
                   private var assos: MutableList<Assos>
) : ArrayAdapter<Assos>(activity, itemResource, assos), Filterable {

    // Copie de la liste mutable, cela permet de référence pour les filtres
    private var listeOriginal : List<Assos> = assos.toList()

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

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

        return layout
    }

    // Function de l'implémentation Filterable
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Assos>()
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""

                if (query.isEmpty()) {
                    filteredList.addAll(listeOriginal)
                } else {
                    listeOriginal.forEach { asso ->
                        if (asso.getAssosName().lowercase().contains(query)
                            || asso.getAcronyme().lowercase().contains(query)) {
                            filteredList.add(asso)
                        }
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                    count = filteredList.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values is List<*>) {
                    clear()
                    addAll(results.values as List<Assos>)
                    notifyDataSetChanged()
                }
            }
        }
    }
}