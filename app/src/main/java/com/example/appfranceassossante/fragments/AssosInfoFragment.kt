package com.example.appfranceassossante.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appfranceassossante.R
import com.example.appfranceassossante.apiService.GetAssosByNameTask
import com.example.appfranceassossante.fragments.don.DonFragment
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AssosInfoFragment : BaseFragment() {
    private lateinit var btnMore: Button
    private lateinit var btnDon: Button
    private lateinit var imgAssos: ImageView
    private lateinit var tvNomAssos: TextView
    private lateinit var infoAssos: TextView
    private var nomAssos: String? = null
    private lateinit var imgBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // recupere le nom de l'assos sur lequel le user a cliqué
        arguments?.let {
            nomAssos = it.getString("nom_assos")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_assos_info, container, false)

        btnMore = view.findViewById(R.id.btnMore)
        btnDon = view.findViewById(R.id.btnDon)
        imgAssos = view.findViewById(R.id.imgAssos)
        tvNomAssos = view.findViewById(R.id.nomAssos)
        infoAssos = view.findViewById(R.id.infoAssos)
        imgBack = view.findViewById(R.id.imgBack)

        // image retour
        imgBack.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, AssosFragment())
            transaction.commit()
        }

        // si nom assos cliqué est pas null
        if (nomAssos != null) {
            tvNomAssos.text = nomAssos
            fetchAssosInfo(nomAssos!!)
        }

        // btn don redirige vers la page de don avec l'assoss préselectionner sur le spinner
        btnDon.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nom_assos", nomAssos) // prends le nom de l'assos

            val fragment = DonFragment()
            fragment.arguments = bundle // passe le nom en argument pour que DonFragment puisse le recuperer (pour le spinner)

            // selectionne l'item don dans le menu
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav?.selectedItemId = R.id.navigation_don

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // btn more qui renvoie sur le site de l'assos (parfois sur la page FranceAssosSanté de l'assos mdr)
        btnMore.setOnClickListener {
            lifecycleScope.launch {
                val fetchedAssos = nomAssos?.let { it1 ->
                    GetAssosByNameTask().getAssosByNameInBG(it1)
                }

                val imageUrl = fetchedAssos?.getImg()

                if (imageUrl != null) {
                    val urlParts = imageUrl.split("/")

                    if (urlParts.size >= 3) {
                        val baseUrl = "${urlParts[0]}//${urlParts[2]}"

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl))
                        startActivity(intent)
                    } else {
                        Log.e("AssosInfo", "URL de l'image non valide pour redirection")
                    }
                } else {
                    Log.e("AssosInfo", "Aucune image trouvée pour l'association")
                }
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    // recup les infos de l'assos et les mets dans les textview
    private fun fetchAssosInfo(nom: String) {
        Log.d("AssosInfo", "Récupération des infos pour: $nom")

        GlobalScope.launch(Dispatchers.Main) {
            val fetchedAssos = GetAssosByNameTask().getAssosByNameInBG(nom)

            if (fetchedAssos != null) {
                tvNomAssos.text = fetchedAssos.getAssosName()
                infoAssos.text = fetchedAssos.getDescription()

                context?.let {
                    Glide.with(it)
                        .load(fetchedAssos.getImg())
                        .into(imgAssos)
                }

            } else {
                Log.e("AssosInfo", "L'association n'a pas été trouvée.")
            }
        }
    }
}