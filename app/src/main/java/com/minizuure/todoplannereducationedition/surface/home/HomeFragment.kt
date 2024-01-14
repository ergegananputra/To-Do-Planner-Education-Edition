package com.minizuure.todoplannereducationedition.surface.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentHomeBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_TASK
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.recycler.adapter.MainTaskAdapter
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.launch
import java.time.ZonedDateTime


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val todayMainTaskAdapter by lazy {
        MainTaskAdapter(
            currentDate = DatetimeAppManager().getLocalDateTime(),
            scope = lifecycleScope,
            sessionViewModel = sessionViewModel,
            routineViewModel = routineViewModel,
            taskViewModel = taskViewModel,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenDetail(it)}
        )
    }

    private fun setOnClickOpenDetail(taskTable: TaskTable) {
        val destination = HomeFragmentDirections.actionHomeFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL,
            title = taskTable.title,
            id = taskTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime())
        )
        findNavController().navigate(destination)
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
        setupViewModelFactory()

        setupEfabAddTask()
        setupTodayRecyclerView() // TODO: Setup today recycler for NoteTask recycler

        // TODO: Setup upcoming recyler



        // NOTE: Testing
        androidDevelopmentAlarmTest()
    }

    private fun androidDevelopmentAlarmTest() {
        val scheduler = AndroidAlarmManager(requireActivity())
        val item = ItemAlarmQueue(
            id = 1,
            action = "ACTION",
            time = ZonedDateTime.now().plusSeconds(5),
            message = "MESSAGE",
            monthCreated = 1,
        )

        binding.drawerToday.setOnClickListener {
            scheduler.schedule(item)
        }

        binding.drawerUpcoming.setOnClickListener {
            scheduler.cancel(item)
        }
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

        val noteFactory = NoteViewModelFactory(app.appRepository)
        noteViewModel = ViewModelProvider(requireActivity(), noteFactory)[NoteViewModel::class.java]

    }

    private fun setupTodayRecyclerView() {
        binding.recyclerViewToday.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayMainTaskAdapter
        }

        updateAdapter()
    }

    private fun updateAdapter() {
        lifecycleScope.launch {
            val todayTask = taskViewModel.getByIndexDay(DatetimeAppManager().getTodayDayId())
            todayMainTaskAdapter.submitList(todayTask)
        }
    }

    private fun setupEfabAddTask() {
        binding.efabAddTask.setOnClickListener {
            val destination = HomeFragmentDirections
                .actionHomeFragmentToTaskManagementActivity(
                    actionToOpen = OPEN_TASK,
                    title = null,
                    selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime())
                )
            findNavController().navigate(destination)
        }


        binding.nestedScrollViewHome.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.efabAddTask.shrink()
            } else {
                binding.efabAddTask.extend()
            }
        }
    }


}