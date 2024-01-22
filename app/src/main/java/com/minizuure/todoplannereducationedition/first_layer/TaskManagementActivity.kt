package com.minizuure.todoplannereducationedition.first_layer

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityTaskManagementBinding
import com.minizuure.todoplannereducationedition.dialog_modal.ActionMoreTaskBottomDialogFragment
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailFragment
import com.minizuure.todoplannereducationedition.first_layer.task.TaskFragment
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_TASK_ID

class TaskManagementActivity : AppCompatActivity() {
    private val args : TaskManagementActivityArgs by navArgs()
    private lateinit var taskNavController : NavController

    val binding by lazy {
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
        val previousBackStackEntry = taskNavController.previousBackStackEntry
        return if (previousBackStackEntry?.destination?.id == taskNavController.graph.startDestinationId) {
            finish()
            true
        } else {
            taskNavController.navigateUp() || super.onSupportNavigateUp()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val previousBackStackEntry = taskNavController.previousBackStackEntry
        if (previousBackStackEntry?.destination?.id == taskNavController.graph.startDestinationId) {
            finish()
        } else {
            if (!taskNavController.navigateUp()) {
                super.onBackPressed()
            }
        }
    }

    private fun setupFragment(toOpen: String) {
        val destination = when(toOpen) {
            OPEN_TASK -> {
                DummyFragmentDirections.actionDummyFragmentToTaskFragment(
                    taskId = args.id
                )
            }
            OPEN_DETAIL -> {
                DummyFragmentDirections.actionDummyFragmentToDetailFragment(
                    taskDetailId = args.id,
                    titleDetail = args.title ?: "",
                    selectedDatetimeDetailIso = args.selectedDatetimeISO,
                    setGoTo = null
                )
            }
            OPEN_DETAIL_GO_TO_QUIZ -> {
                DummyFragmentDirections.actionDummyFragmentToDetailFragment(
                    taskDetailId = args.id,
                    titleDetail = args.title ?: "",
                    selectedDatetimeDetailIso = args.selectedDatetimeISO,
                    setGoTo = OPEN_DETAIL_GO_TO_QUIZ
                )
            }
            OPEN_DETAIL_GO_TO_PACK  -> {
                DummyFragmentDirections.actionDummyFragmentToDetailFragment(
                    taskDetailId = args.id,
                    titleDetail = args.title ?: "",
                    selectedDatetimeDetailIso = args.selectedDatetimeISO,
                    setGoTo = OPEN_DETAIL_GO_TO_PACK
                )
            }
            else -> {
                null
            }
        }

        if (destination == null) {
            finish()
        } else {
            taskNavController.navigate(destination)
        }
    }

    fun setToolbarTitle(fragment : Fragment, customTitle: String = "") {
        // TODO: Tambahkan opsi untuk fragment lain
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

            else -> changeToolbarTitle("Untitled", false)
        }

        setupNavBarActionMenu(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_detail_menu, menu)
        return true
    }

    private fun setupNavBarActionMenu(fragment: Fragment) {
        when (fragment) {
            is DetailFragment -> {
                setupDetailNavBarActionMenu(fragment)
            }
            is TaskFragment -> {
                // Todo : Setup Task NavBar Action Menu for TaskFragment
            }
        }
    }

    private fun setupDetailNavBarActionMenu(fragment: DetailFragment) {
        binding.toolbarDetail.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_more -> {
                    val bottomSheet = ActionMoreTaskBottomDialogFragment(
                        taskId = args.id,
                        onEditAction = fragment.setOnEditTaskAction(),
                        onDeleteAction = fragment.setOnDeleteTaskAction(),
                        onResetAction = fragment.setOnResetTaskAction(),
                    )
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    true
                }

                else -> false
            }
        }
    }



    private fun changeToolbarTitle(title: String, isCollapsable: Boolean = true) {
        if (isCollapsable) {
            Log.d("TaskManagementActivity", "changeToolbarTitle collapsable: $title")

            binding.collapsingToolbarTaskManagement.visibility = View.VISIBLE
            binding.toolbarNonCollapsingTaskManagement.visibility = View.GONE
            if (title == "") {
                binding.collapsingToolbarTaskManagement.title = getString(R.string.title)
            } else {
                binding.collapsingToolbarTaskManagement.title = title
            }

            setSupportActionBar(binding.toolbarDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            Log.d("TaskManagementActivity", "changeToolbarTitle non collapsable: $title")

            binding.collapsingToolbarTaskManagement.visibility = View.GONE
            binding.toolbarNonCollapsingTaskManagement.visibility = View.VISIBLE
            if (title == "") {
                binding.toolbarNonCollapsingTaskManagement.title = getString(R.string.title)
            } else {
                binding.toolbarNonCollapsingTaskManagement.title = title
            }

            setSupportActionBar(binding.toolbarNonCollapsingTaskManagement)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

    }



    companion object {
        const val OPEN_TASK = "com.minizuure.todoplannereducationedition.first_layer.task"
        const val OPEN_DETAIL = "com.minizuure.todoplannereducationedition.first_layer.detail"
        const val OPEN_DETAIL_GO_TO_PACK = "com.minizuure.todoplannereducationedition.first_layer.detail.go_to_pack"
        const val OPEN_DETAIL_GO_TO_QUIZ = "com.minizuure.todoplannereducationedition.first_layer.detail.go_to_quiz"
        const val OPEN_SCHEDULE = "com.minizuure.todoplannereducationedition.first_layer.schedule"
    }
}