package com.example.appfranceassossante.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SearchView
import com.example.appfranceassossante.models.Assos
import com.example.appfranceassossante.AssosAdapter
import com.example.appfranceassossante.R

class AssosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_assos, container, false)

        val gridView : GridView = view.findViewById(R.id.grilleAssos)

        val assosListe = listOf(
            Assos("A.M.I", R.drawable.ami_logo),
            Assos("France Dépression", R.drawable.francedepression_logo),
            Assos("AAAVAM",R.drawable.aaavam_logo),
            Assos("ADDICTIONS ALCOOL VIE LIBRE", R.drawable.addiction_alcool_vie_libre_logo),
            Assos("ADEPA",R.drawable.adepa_logo)
        )

        val adapter = AssosAdapter(requireContext(), R.layout.item_asso, assosListe)

        gridView.adapter = adapter
//        setDynamicHeight(gridView, 3)

        val searchV : SearchView = view.findViewById(R.id.search_bar)
        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)

                return false
            }

        })
        return view
    }

//    fun setDynamicHeight(gridView: GridView, columns: Int) {
//        val listAdapter = gridView.adapter ?: return
//
//        var totalHeight = 0
//        val items = listAdapter.count
//        val rows = Math.ceil((items / columns.toDouble())).toInt()
//
//        for (i in 0 until rows) {
//            val listItem = listAdapter.getView(i, null, gridView)
//            listItem.measure(0, 0)
//            totalHeight += listItem.measuredHeight
//        }
//
//        val params = gridView.layoutParams
//        params.height = totalHeight + (gridView.verticalSpacing * (rows - 1))
//        gridView.layoutParams = params
//        gridView.requestLayout()
//    }

//    fun setAdapterFilter(Text : String) : String {
//
//    }
}