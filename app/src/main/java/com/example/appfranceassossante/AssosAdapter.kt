package com.example.appfranceassossante

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class AssosAdapter(activity: Activity, itemResource: Int, habitats : List<Assos>) :
    ArrayAdapter<Assos?>(activity, itemResource, habitats) {
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

    }
}