package com.example.appfranceassossante

import android.widget.ArrayAdapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup

class AssosAdapter(activity: Activity, itemResource: Int, habitats : List<Assos>) :
    ArrayAdapter<Assos?>(activity, itemResource, habitats) {
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

    }
}