package com.minizuure.todoplannereducationedition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minizuure.todoplannereducationedition.databinding.ActivityMainBinding
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailActivity

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private val launcherToDetail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "onCreate: RESULT_OK")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this).statusBarTweak()

        // Bottom Navigation
        with(binding) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_surface) as NavHostFragment
            val navController  = navHostFragment.navController
            bottomNavigationViewSurface.setupWithNavController(navController)
        }

        byPassTesting()
    }

    private fun byPassTesting() {
        val intentToDetail = Intent(this, DetailActivity::class.java)
        launcherToDetail.launch(intentToDetail)
    }
}