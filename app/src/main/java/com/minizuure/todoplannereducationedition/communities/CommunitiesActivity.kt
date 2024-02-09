package com.minizuure.todoplannereducationedition.communities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.databinding.ActivityCommunitiesBinding

class CommunitiesActivity : AppCompatActivity() {

    private lateinit var communitiesNavController : NavController

    private val binding by lazy {
        ActivityCommunitiesBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentComunities.id) as NavHostFragment
        communitiesNavController = navHostFragment.navController

        setSupportActionBar(binding.toolbarComunities)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""

        CustomSystemTweak(this).statusBarTweak()
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (communitiesNavController.currentDestination?.id == communitiesNavController.graph.startDestinationId) {
            finish()
            true
        } else {
            communitiesNavController.navigateUp() || super.onSupportNavigateUp()
        }
    }

}