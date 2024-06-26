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
import androidx.transition.TransitionManager
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentHomeBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_PACK
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_QUIZ
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_TASK
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.recycler.adapter.MainTaskAdapter
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val todayMainTaskAdapter by lazy {
        MainTaskAdapter(
            scope = lifecycleScope,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenTask(it, OPEN_DETAIL)},
            onClickOpenQuizInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_QUIZ)},
            onClickOpenToPackInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_PACK)}
        )
    }

    private val upComingMainTaskAdapter by lazy {
        MainTaskAdapter(
            scope = lifecycleScope,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenTask(it, OPEN_DETAIL)},
            onClickOpenQuizInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_QUIZ)},
            onClickOpenToPackInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_PACK)}
        )
    }



    private fun setOnClickOpenTask(taskAndSessionJoinTable: TaskAndSessionJoin, action : String) {
        val destination = HomeFragmentDirections.actionHomeFragmentToTaskManagementActivity(
            actionToOpen = action,
            title = taskAndSessionJoinTable.title,
            id = taskAndSessionJoinTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(
                DatetimeAppManager(
                    taskAndSessionJoinTable.paramsSelectedIso8601Date
                ).selectedDetailDatetimeISO
            ),
            indexDay = taskAndSessionJoinTable.indexDay,
            sessionId = taskAndSessionJoinTable.fkSessionId
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
        setupTodaySection()
        setupTodayRecyclerView()

        setupUpcomingSection()
        setupUpcomingRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        updateUpcomingAdapter()
        updateQuizAdapter()
    }



    private fun setupUpcomingSection() {
        binding.buttonExpandableUpcoming.setOnClickListener {
            upcomingDrawerAnimation()
        }
        binding.drawerUpcoming.setOnClickListener {
            upcomingDrawerAnimation()
        }
        binding.textViewDrawerUpcoming.setOnClickListener {
            upcomingDrawerAnimation()
        }
    }

    private fun upcomingDrawerAnimation() {
        TransitionManager.beginDelayedTransition(binding.constraintLayoutHome)
        if (binding.recyclerViewUpcoming.visibility == View.VISIBLE) {
            binding.recyclerViewUpcoming.visibility = View.GONE
            binding.buttonExpandableUpcoming.isActivated = true
        } else {
            binding.recyclerViewUpcoming.visibility = View.VISIBLE
            binding.buttonExpandableUpcoming.isActivated = false
        }
    }

    private fun setupTodaySection() {
        binding.buttonExpandableToday.setOnClickListener {
            todayDrawerAnimation()
        }
        binding.drawerToday.setOnClickListener {
            todayDrawerAnimation()
        }
        binding.textViewDrawerToday.setOnClickListener {
            todayDrawerAnimation()
        }
    }

    private fun todayDrawerAnimation() {
        TransitionManager.beginDelayedTransition(binding.constraintLayoutHome)
        if (binding.recyclerViewToday.visibility == View.VISIBLE) {
            binding.recyclerViewToday.visibility = View.GONE
            binding.buttonExpandableToday.isActivated = true
        } else {
            binding.recyclerViewToday.visibility = View.VISIBLE
            binding.buttonExpandableToday.isActivated = false
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

    private fun setupUpcomingRecyclerView() {
        binding.recyclerViewUpcoming.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = upComingMainTaskAdapter
        }


        updateUpcomingAdapter()
    }

    private fun updateUpcomingAdapter() {
        lifecycleScope.launch {
            val today = DatetimeAppManager().selectedDetailDatetimeISO
            val upcomingTask = mutableListOf<TaskAndSessionJoin>()

            val dateDayOne = DatetimeAppManager(today.plusDays(1))
            val dateDayTwo = DatetimeAppManager(today.plusDays(2))
            val dateDayThree = DatetimeAppManager(today.plusDays(3))


            val dayOne = withContext(Dispatchers.IO) {
                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = dateDayOne.getTodayDayId(),
                    selectedDateTime = dateDayOne.selectedDetailDatetimeISO,
                    isToday = false
                )
            }
            val dayTwo = withContext(Dispatchers.IO) {
                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = dateDayTwo.getTodayDayId(),
                    selectedDateTime = dateDayTwo.selectedDetailDatetimeISO,
                    isToday = false
                )
            }
            val dayThree = withContext(Dispatchers.IO) {
                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = dateDayThree.getTodayDayId(),
                    selectedDateTime = dateDayThree.selectedDetailDatetimeISO,
                    isToday = false
                )
            }

            upcomingTask.addAll(dayOne + dayTwo + dayThree)

            upComingMainTaskAdapter.submitList(upcomingTask)
        }
    }

    private fun setupTodayRecyclerView() {
        binding.recyclerViewToday.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = todayMainTaskAdapter
        }

        updateQuizAdapter()
    }

    private fun updateQuizAdapter() {
        lifecycleScope.launch {
            val todayTask = withContext(Dispatchers.IO) {
                val today = DatetimeAppManager()
                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = today.getTodayDayId(),
                    selectedDateTime = today.selectedDetailDatetimeISO,
                    isToday = true
                )
            }
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