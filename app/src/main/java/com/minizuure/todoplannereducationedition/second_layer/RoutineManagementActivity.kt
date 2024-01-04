package com.minizuure.todoplannereducationedition.second_layer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityRoutineManagementBinding
import com.minizuure.todoplannereducationedition.second_layer.routines.RoutineFormFragment
import com.minizuure.todoplannereducationedition.second_layer.routines.RoutinesFragment

class RoutineManagementActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRoutineManagementBinding.inflate(layoutInflater)
    }
    private val routineNavController by lazy {
        findNavController(R.id.nav_host_fragment_routine_management)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarRoutineManagement)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CustomSystemTweak(this).statusBarTweak()


    }

    override fun onSupportNavigateUp(): Boolean {
        val routineNavController = findNavController(R.id.nav_host_fragment_routine_management)
        return if (routineNavController.currentDestination?.id == routineNavController.graph.startDestinationId) {
            finish()
            true
        } else {
            routineNavController.navigateUp() || super.onSupportNavigateUp()
        }
    }


    /**
     * Change toolbar title from fragment.
     * Please set this function in onViewCreated() of fragment.
     * - example how to use:
     *   ```kotlin
     *      (activity as RoutineManagementActivity).setToolbarTitle(this)
     *   ```
     * @param fragment: Fragment
     * @return Unit
     */
    fun setToolbarTitle(fragment : Fragment) {
        //TODO: Tambahkan opsi untuk fragment lain
        when (fragment) {
            is RoutinesFragment -> {
                binding.toolbarRoutineManagement.title = getString(R.string.routines_management)
            }
            is RoutineFormFragment -> {
                val title = if (fragment.args.routineId == 0) {
                    getString(R.string.new_routine)
                } else {
                    getString(R.string.edit_routine)
                }

                changeToolbarTitle(title)
            }


            else -> changeToolbarTitle()
        }
    }


    private fun changeToolbarTitle(title: String = "") {
        if (title == "") {
            binding.toolbarRoutineManagement.title = getString(R.string.routines_management)
        } else {
            binding.toolbarRoutineManagement.title = title
        }
    }



}