package com.minizuure.todoplannereducationedition

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minizuure.todoplannereducationedition.databinding.ActivityMainBinding
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
import com.minizuure.todoplannereducationedition.services.permissions.Permissions
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this)
            .statusBarTweak()
            .customNavigationBarColorSet(com.google.android.material.R.attr.colorSurfaceContainer)


        // Bottom Navigation
        with(binding) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_surface) as NavHostFragment
            val navController  = navHostFragment.navController
            bottomNavigationViewSurface.setupWithNavController(navController)
        }

        optimizeSessionTaskProviderDatabase()

        checkForPermission()
        checkThemeMode()
        syncronizeDatabase()
    }

    private fun syncronizeDatabase() {
        // TODO: Implement syncronizeDatabase only if the user join experiment
    }

    private fun checkThemeMode() {
        val currentThemeMode = AppCompatDelegate.getDefaultNightMode()
        UserPreferences(this).isThemeDark = currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun checkForPermission() {
        val permissions = Permissions(this)
        val isFirstTime = UserPreferences(this).firstTime
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU && isFirstTime) {
            val result = permissions.requestPermissionNotification()

            result.let {
                if (!it) {
                    permissions.requestPermissionAlarm()
                }
            }
            Log.d("MainActivity", "checkForPermission: $result")
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && isFirstTime) {
            permissions.requestPermissionAlarm()
        }

        UserPreferences(this).firstTime = false
    }

    private fun optimizeSessionTaskProviderDatabase() {
        val app = application as ToDoPlannerApplication
        val factory = SessionTaskProviderViewModelFactory(app.appRepository)
        val sessionTaskProviderViewModel = ViewModelProvider(this, factory)[SessionTaskProviderViewModel::class.java]

        sessionTaskProviderViewModel.optimizeSessionTaskProviderTable()
    }


}