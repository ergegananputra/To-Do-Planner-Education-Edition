package com.minizuure.todoplannereducationedition.first_layer.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentDetailBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_DETAIL_ID = "task_detail_id"
private const val ARG_DETAIL_TITLE = "title_detail"
private const val ARG_DETAIL_SELECTED_DATE = "selected_datetime_detail_iso"
class DetailFragment : Fragment() {
    val args : DetailFragmentArgs by navArgs()
    private lateinit var navController: NavController

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel

    private val binding by lazy {
        FragmentDetailBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as TaskManagementActivity).setToolbarTitle(this, args.titleDetail)
        navController = Navigation.findNavController(view)
        setupViewModelFactory()

        lifecycleScope.launch {
            val task = taskViewModel.getById(args.taskDetailId) ?: return@launch closeFragment()
            val session = sessionViewModel.getById(task.sessionId) ?: return@launch closeFragment()
            val routine = routineViewModel.getById(session.fkRoutineId) ?: return@launch closeFragment()


            setupDate()
            setupTime(task, session)
            setupLocation(task)
            setupRoutineTemplateText(routine)
        }
    }

    private fun setupRoutineTemplateText(routine: RoutineTable) {
        val routineNameText = routine.title
        binding.textViewRoutineName.text = routineNameText
    }

    private fun setupLocation(task: TaskTable) {
        if (task.locationName == null) return

        binding.textViewLocation.text = task.locationName!!

        binding.cardViewSessionInfo.setOnClickListener {
            val link = task.locationAddress ?: return@setOnClickListener

            if(link.contains("maps")) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Open another app")
                    .setMessage("Do you want to open this link in web browser or goole maps? $link")
                    .setNegativeButton("Yes") { _, _ ->
                        val intentToGoogleMaps = Intent(Intent.ACTION_VIEW, link.toUri())
                        startActivity(intentToGoogleMaps)
                    }
                    .setPositiveButton("No") { _, _ -> }
            }
        }
    }

    private fun setupTime(task: TaskTable, session: SessionTable) {
        val timeIntervalText = if (task.isCustomSession) {
            "${task.startTime} - ${task.endTime}"
        } else {
            "${session.timeStart} - ${session.timeEnd}"
        }

        binding.textViewSessionTime.text = timeIntervalText
    }

    private fun closeFragment() {
        (requireActivity() as TaskManagementActivity).onSupportNavigateUp()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

    }

    private fun setupDate() {
        val date = DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime).toReadable()
        binding.textViewDatetime.text = date
    }

    fun setOnEditTaskAction(): () -> Unit = {
        // Navigate to Task Form
        val destination = DetailFragmentDirections
            .actionDetailFragmentToTaskFragment(
                taskId = args.taskDetailId,
            )

        navController.navigate(destination)
    }

    fun setOnDeleteTaskAction(): () -> Unit = {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_task_warning_title)
            .setMessage(getString(R.string.delete_task_warning_message_confirmation))
            .setPositiveButton(R.string.cancel) { _, _ -> }
            .setNegativeButton(R.string.delete) { _, _ ->
                lifecycleScope.launch(Dispatchers.Main) {
                    this.launch(Dispatchers.IO) {
                        taskViewModel.deleteById(args.taskDetailId)
                    }
                    closeFragment()
                }
            }
            .show()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param taskDetailId : Long (default = -1L)
         * @param titleDetail : String (name of the task)
         * @return A new instance of fragment DetailFragment.
         */
        @JvmStatic
        fun newInstance(taskDetailId: Long = -1L, titleDetail: String, selectedDetailDatetimeISO : ParcelableZoneDateTime) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DETAIL_ID, taskDetailId)
                    putString(ARG_DETAIL_TITLE, titleDetail)
                    putParcelable(ARG_DETAIL_SELECTED_DATE, selectedDetailDatetimeISO)
                }
            }
    }


}