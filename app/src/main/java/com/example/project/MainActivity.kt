package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project.databinding.ActivityMainBinding
import com.example.project.ui.gallery.GalleryFragment
import com.example.project.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var type: String
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar



    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        Snackbar.make(findViewById(android.R.id.content), "Sesión iniciada", Snackbar.LENGTH_LONG)
            .show()

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        type = sharedPreferences.getString("type", null).toString()

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.WHITE;

        toolbar = binding.appBarMain.toolbar
        toolbar.title = ""
        toolbar.elevation = 0F
        binding.appBarMain.toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.appBarMain.toolbar.title = "Title"

        binding.appBarMain.fab.setOnClickListener {
            val intent = Intent( this, sendEmail::class.java).apply {
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



        val drawerLayout: DrawerLayout = binding.drawerLayout


        val navView: NavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.disconnect
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        configToolbar()
    }



    fun configToolbar() {
        toolbar.post() {
            val d = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_menu_24black, null)
            toolbar.navigationIcon = d
            toolbar.elevation = 0F
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.ids)
        if (type != "Organizador") {
            item.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disconnect -> {
                val intent = Intent(this, Login::class.java).apply {
                    putExtra("close", "close")
                }
                saveData("sincorreo",false,"falso")
                startActivity(intent)
                val myService = Intent(this@MainActivity, MyService::class.java)
                stopService(myService)
                finish()
            }

            R.id.ids -> {
                val intent = Intent(this, CreateCategory::class.java).apply {
                }
                startActivity(intent)
            }
            /*R.id.infoAdd -> {
                val intent = Intent(this, infoAdd::class.java).apply {
                }
                startActivity(intent)
            }*/

        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveData (correo:String, online:Boolean, type: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("correo", correo)
            putString("type", type)
            putBoolean("online", online)
        }.apply()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getConext (): Context {
        return this@MainActivity
    }



}