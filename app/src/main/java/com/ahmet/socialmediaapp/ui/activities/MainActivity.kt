package com.ahmet.socialmediaapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.socialmediaapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navController=findNavController(R.id.main_fragmentContainerView)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.searchFragment,
                R.id.userProfileFragment
            )
        )

        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController,appBarConfiguration)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()||super.onSupportNavigateUp()
    }

}