package com.example.appfranceassossante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonationAdapter(private val donations: List<Don>) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_don, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.bind(donation)
    }

    override fun getItemCount(): Int {
        return donations.size
    }

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val associationTextView: TextView = itemView.findViewById(R.id.tvAssociation)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDate)
        private val montantTextView: TextView = itemView.findViewById(R.id.tvMontant)
        private val paiementTextView: TextView = itemView.findViewById(R.id.tvPaiement)

        fun bind(donation: Don) {
            associationTextView.text = donation.association
            dateTextView.text = donation.date.toString()
            montantTextView.text = "${donation.montant}€"
            paiementTextView.text = donation.paiement
        }
    }
}
