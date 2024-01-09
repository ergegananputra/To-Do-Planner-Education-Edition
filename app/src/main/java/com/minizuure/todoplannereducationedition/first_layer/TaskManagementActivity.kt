package com.minizuure.todoplannereducationedition.first_layer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityTaskManagementBinding
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailFragment
import com.minizuure.todoplannereducationedition.first_layer.task.TaskFragment

class TaskManagementActivity : AppCompatActivity() {
    private val args : TaskManagementActivityArgs by navArgs()
    private lateinit var taskNavController : NavController

    private val binding by lazy {
        ActivityTaskManagementBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentContainerViewTask.id) as NavHostFragment
        taskNavController = navHostFragment.navController

        CustomSystemTweak(this).statusBarTweak()

        setupFragment(args.actionToOpen)
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (taskNavController.currentDestination?.id == taskNavController.graph.startDestinationId) {
            finish()
            true
        } else {
            taskNavController.navigateUp() || super.onSupportNavigateUp()
        }
    }

    private fun setupFragment(toOpen: String) {
        val fragment = when(toOpen) {
            OPEN_TASK -> {
                TaskFragment.newInstance(args.id)

            }
            OPEN_DETAIL -> {
                DetailFragment.newInstance(args.id, args.title ?: "")
            }

            else -> {
                TaskFragment.newInstance()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.navHostFragmentContainerViewTask.id, fragment)
            .commit()
    }

    fun setToolbarTitle(fragment : Fragment, customTitle: String = "") {
        //TODO: Tambahkan opsi untuk fragment lain
        when (fragment) {
            is DetailFragment -> {
                changeToolbarTitle(customTitle)
            }
            is TaskFragment -> {
                val title = if (fragment.args.taskId == DEFAULT_TASK_ID) {
                    getString(R.string.new_task)
                } else {
                    getString(R.string.edit_task)
                }
                changeToolbarTitle(title,false)
            }

            else -> changeToolbarTitle()
        }
    }



    private fun changeToolbarTitle(title: String = "", isCollapsable: Boolean = true) {
        if (isCollapsable) {
            setSupportActionBar(binding.toolbarDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.collapsingToolbarTaskManagement.visibility = View.VISIBLE
            binding.toolbarNonCollapsingTaskManagement.visibility = View.GONE
            if (title == "") {
                binding.collapsingToolbarTaskManagement.title = getString(R.string.title)
            } else {
                binding.collapsingToolbarTaskManagement.title = title
            }
        } else {
            setSupportActionBar(binding.toolbarNonCollapsingTaskManagement)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.collapsingToolbarTaskManagement.visibility = View.GONE
            binding.toolbarNonCollapsingTaskManagement.visibility = View.VISIBLE
            if (title == "") {
                binding.toolbarNonCollapsingTaskManagement.title = getString(R.string.title)
            } else {
                binding.toolbarNonCollapsingTaskManagement.title = title
            }
        }

    }



    companion object {
        const val DEFAULT_TASK_ID = -1L

        const val OPEN_TASK = "com.minizuure.todoplannereducationedition.first_layer.task"
        const val OPEN_DETAIL = "com.minizuure.todoplannereducationedition.first_layer.detail"
        const val OPEN_SCHEDULE = "com.minizuure.todoplannereducationedition.first_layer.schedule"
    }
}