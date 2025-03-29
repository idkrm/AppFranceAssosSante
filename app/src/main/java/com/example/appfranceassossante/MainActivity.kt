package com.example.appfranceassossante

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.appfranceassossante.fragments.AccessibiliteFragment
import com.example.appfranceassossante.fragments.AccueilFragment
import com.example.appfranceassossante.fragments.AssosFragment
import com.example.appfranceassossante.fragments.don.DonFragment
import com.example.appfranceassossante.fragments.SeConnecterFragment
import com.example.appfranceassossante.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
import com.example.appfranceassossante.apiService.GetUserTask
import com.example.appfranceassossante.apiService.CreateUserTask
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.appfranceassossante.utilsAccessibilite.textSize.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {
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
        // TEST
        val email = "johndoe@example.com"
        val getUserTask = GetUserTask()
        lifecycleScope.launch {
            try {
                val user = getUserTask.getUserInBG(email)
                when {
                    user == null -> Log.d("UserInfo", "Aucune donnée utilisateur trouvée")
                    email == (user.email) ->
                        {Log.d("UserInfo", "Utilisateur trouvée")
                            Log.d("UserInfo", "Nom Complet: ${user.nom} ${user.prenom}")
                        }
                    email != (user.email) -> Log.d("UserInfo", "Utilisateur ne correspond pas a l'email demandé")
                }
            } catch (e: Exception) {
                Log.e("Login", getString(R.string.error_connexion), e)
            }
        }
//        val getAssosTask = GetAssosTask { associations ->
//            // Vérifier si la liste n'est pas vide
//            if (associations.isNotEmpty()) {
//                // Afficher les associations dans le logcat
//                associations.forEach { association ->
//                    Log.d("MainActivity", "Association récupérée: ${association.getAssosName()}, ${association.getAcronyme()}")
//                }
//            } else {
//                Log.d("MainActivity", "Aucune association trouvée")
//            }
//        }
//
//        // Exécuter la tâche pour récupérer les associations
//        getAssosTask.execute()

        //FIN TEST
    }
    // Fonction pour appeler la tâche de création d'utilisateur
    private fun createUser(user: User) {
        // Lancer la coroutine dans le scope global
        lifecycleScope.launch {
            try {
                val createUserTask = CreateUserTask(this@MainActivity)
                val result = createUserTask.createUserInBG(user)
                withContext(Dispatchers.Main) {
                    handleCreateUserResult(result)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating user", e)
                withContext(Dispatchers.Main) {
                    // Afficher un message d'erreur à l'utilisateur
                }
            }
        }
    }

    private fun handleCreateUserResult(result: Boolean) {
        if (result) {
            Log.d(TAG, "Utilisateur a été créé avec succès")
            // Naviguer vers l'écran suivant ou afficher un message de succès
        } else {
            Log.d(TAG, "Echec de la création de l'utilisateur")
            // Afficher un message d'erreur à l'utilisateur
        }
    }

    // Fonction pour traiter la réponse après l'exécution de la tâche
    private fun onPostExecute(result: String) {
        Log.d("CreateUserTask", result)  // Affiche le résultat dans le log
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
        val sharedPreferences= context.getSharedPreferences("Settings", MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "fr") ?: "fr"

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }


}
