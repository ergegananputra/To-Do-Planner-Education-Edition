package com.minizuure.todoplannereducationedition.settings_information

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.databinding.ActivitySettingsInformationBinding

class SettingsInformationActivity : AppCompatActivity() {
    private lateinit var settingsNavController: NavController

    private val binding by lazy {
        ActivitySettingsInformationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentContainerViewSettings.id) as NavHostFragment
        settingsNavController = navHostFragment.navController

        setSupportActionBar(binding.toolbarDetailSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CustomSystemTweak(this).statusBarTweak()

    }

    override fun onSupportNavigateUp(): Boolean {
        return if (settingsNavController.currentDestination?.id == settingsNavController.graph.startDestinationId) {
            finish()
            true
        } else {
            settingsNavController.navigateUp() || super.onSupportNavigateUp()
        }
    }


}