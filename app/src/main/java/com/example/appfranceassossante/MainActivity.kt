package com.example.appfranceassossante

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        loadFragment(AccueilFragment()) // par defaut c'est le fragment de l'accueil

        // permet de naviguer entre les fragments
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_accueil -> AccueilFragment()
                R.id.navigation_assoc -> AssosFragment()
                R.id.navigation_don -> DonFragment()
                R.id.navigation_profil -> ProfilFragment()
                else -> AccueilFragment()
            }

            // ajuste la couleur de fond du header
            val nomPage = findViewById<TextView>(R.id.page)
            val headerColor = findViewById<RelativeLayout>(R.id.header)

            when (item.itemId) { // la page accueil met le fond du header en bleu, le reste en blanc
                R.id.navigation_accueil -> {
                    nomPage.text = "France Assos Santé"
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.fond_bleu))
                }
                R.id.navigation_assoc -> {
                    nomPage.text = "Les associations"
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.navigation_don -> {
                    nomPage.text = "Faire un don"
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.navigation_profil -> {
                    nomPage.text = "Se connecter"
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
            }

            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
