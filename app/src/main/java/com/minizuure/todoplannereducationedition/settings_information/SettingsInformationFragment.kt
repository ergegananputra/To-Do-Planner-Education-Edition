package com.minizuure.todoplannereducationedition.settings_information

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentSettingBinding
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.permissions.Permissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsInformationFragment : Fragment() {
    private lateinit var permissions: Permissions
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var routineViewModel : RoutineViewModel


    private val binding by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        permissions = Permissions(requireActivity())
        setupViewModelFactory()
        return binding.root
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTaskAndRoutine()
        setupPushNotificationPermission()
        setupAlarmPermission()
    }

    private fun setupTaskAndRoutine() {
        setupTaskAndRoutineDeleteButton()
        setupTextTaskAndRoutine()
    }

    private fun setupTextTaskAndRoutine() {
        lifecycleScope.launch {
            val routineCount = withContext(Dispatchers.IO){ routineViewModel.getCount() }
            val taskCount = withContext(Dispatchers.IO){ taskViewModel.getCount() }

            val text = "You have $routineCount routines and $taskCount tasks"
            binding.textViewDescriptionTasksAndRoutines.text = text
        }
    }

    private fun setupTaskAndRoutineDeleteButton() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setIcon(R.drawable.ic_delete_outline)
            .setTitle("Delete All Tasks and Routines")
            .setMessage("Are you sure you want to delete all tasks and routines? This action cannot be undone.")
            .setPositiveButton("Yes, delete all data") { _, _ ->
                lifecycleScope.launch {
                    val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)
                    app.deleteAllOperation()
                    setupTextTaskAndRoutine()
                }
            }
            .setNegativeButton("No") { _, _ -> }
            .create()

        binding.buttonDeleteTasksAndRoutines.setOnClickListener {
            dialog.show()
        }
    }

    private fun setupAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.cardViewAlertAndReminder.visibility = View.VISIBLE

            binding.cardViewAlertAndReminder.setOnClickListener {
                permissions.requestPermissionAlarm()
            }

        } else {
            binding.cardViewAlertAndReminder.visibility = View.GONE
        }

    }

    private fun setupPushNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.cardViewPushNotification.visibility = View.VISIBLE

            val isGranted = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (isGranted == 0) {
                changePushNotificationSwitch(true)
            } else {
                changePushNotificationSwitch(false)
            }

            binding.cardViewPushNotification.setOnClickListener {
                val result = permissions.requestPermissionNotification()
                changePushNotificationSwitch(result)
            }

        } else {
            binding.cardViewPushNotification.visibility = View.GONE
        }


    }

    private fun changePushNotificationSwitch(result: Boolean) {
        binding.switchPushNotification.isChecked = result
    }



}