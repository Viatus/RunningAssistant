package com.example.runningassistant.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.runningassistant.R

class TrainingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_training) as NavHostFragment

        setupActionBarWithNavController(navHostFragment.navController)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }



    override fun onSupportNavigateUp(): Boolean {
        if (!findViewById<FragmentContainerView>(R.id.nav_host_fragment_training).findNavController()
                .navigateUp()
        ) {
            finish()
        }
        //Really questionable patch to display home button on start fragment
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return true
    }
}