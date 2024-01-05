package com.minizuure.todoplannereducationedition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minizuure.todoplannereducationedition.databinding.ActivityMainBinding
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // TODO : Delete this testing purpose
    private var testingPurpose = true

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private val launcherToDetail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "onCreate: RESULT_OK")
            testingPurpose = false
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

//        byPassTesting(testingPurpose)
    }

    private fun byPassTesting(state : Boolean) {
        val intentToDetail = Intent(this, DetailActivity::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            if (state) {
                this.launch(Dispatchers.Main) {
                    launcherToDetail.launch(intentToDetail)
                }
            }
        }
    }
}