package com.example.appfranceassossante

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        loadFragment(AccueilFragment()) // par defaut c'est le fragment de l'accueil

        // pour ajuster la couleur de fond du header et le titre de la page
        val nomPage = findViewById<TextView>(R.id.page)
        val headerColor = findViewById<RelativeLayout>(R.id.header)

        // permet de naviguer entre les fragments depuis la barre de nav
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_accueil -> AccueilFragment()
                R.id.navigation_assoc -> AssosFragment()
                R.id.navigation_don -> DonFragment()
                R.id.navigation_profil -> SeConnecterFragment()
                else -> AccueilFragment()
            }

            when (item.itemId) { // la page accueil met le fond du header en bleu, le reste en blanc
                R.id.navigation_accueil -> {
                    nomPage.text = getString(R.string.app_name)
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.fond_bleu))
                }
                R.id.navigation_assoc -> {
                    nomPage.text = getString(R.string.assoc)
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.navigation_don -> {
                    nomPage.text = getString(R.string.faire_un_don)
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.navigation_profil -> {
                    nomPage.text = getString(R.string.profil)
                    headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                }
            }

            loadFragment(fragment)
            true
        }

        val access = findViewById<ImageView>(R.id.img_handicap)
        access.setOnClickListener{
            nomPage.text = getString(R.string.accessibilite)
            headerColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            loadFragment(AccessibiliteFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    //applique la langue
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    //recupere la langue et l'applique a l'app
    private fun updateBaseContextLocale(context: Context): Context {
        val sharedPreferences= context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "fr") ?: "fr"

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
