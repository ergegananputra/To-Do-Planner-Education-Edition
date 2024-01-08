package com.minizuure.todoplannereducationedition.second_layer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityRoutineManagementBinding
import com.minizuure.todoplannereducationedition.second_layer.routines.RoutineDetailFragment
import com.minizuure.todoplannereducationedition.second_layer.routines.RoutineFormFragment
import com.minizuure.todoplannereducationedition.second_layer.routines.RoutinesFragment
import com.minizuure.todoplannereducationedition.second_layer.session.SessionFormFragment

class RoutineManagementActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRoutineManagementBinding.inflate(layoutInflater)
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
                val title = getString(R.string.routines_management)
                changeToolbarTitle(title)
            }
            is RoutineFormFragment -> {
                val title = if (fragment.args.routineId == DEFAULT_ROUTINE_ID) {
                    getString(R.string.new_routine)
                } else {
                    getString(R.string.edit_routine)
                }

                changeToolbarTitle(title)
            }
            is SessionFormFragment -> {
                val title = if (fragment.args.sessionId == DEFAULT_SESSION_ID) {
                    getString(R.string.new_session)
                } else {
                    getString(R.string.edit_session)
                }

                changeToolbarTitle(title)
            }
            is RoutineDetailFragment -> {
                val title = getString(R.string.routine_detail)
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

    companion object {
        const val DEFAULT_ROUTINE_ID = -1L
        const val DEFAULT_SESSION_ID = -1L
    }


}