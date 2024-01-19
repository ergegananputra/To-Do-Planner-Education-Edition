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
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZonedDateTime


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val todayMainTaskAdapter by lazy {
        MainTaskAdapter(
            currentDate = DatetimeAppManager().selectedDetailDatetimeISO,
            scope = lifecycleScope,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenDetail(it)},
            onClickOpenQuizInDetail = {setOnClickOpenQuizInDetail(it)},
            onClickOpenToPackInDetail = {setOnClickOpenToPackInDetail(it)}
        )
    }

    private val upComingMainTaskAdapter by lazy {
        MainTaskAdapter(
            currentDate = DatetimeAppManager().selectedDetailDatetimeISO.plusDays(1),
            scope = lifecycleScope,
            notesViewModel = noteViewModel,
            onClickOpenDetail = { setOnClickOpenDetailUpcoming(it)},
            onClickOpenQuizInDetail = {setOnClickOpenQuizInDetailUpcoming(it)},
            onClickOpenToPackInDetail = { setOnClickOpenToPackInDetailUpcoming(it) }
        )
    }

    /**
     * Set on click open to pack in detail upcoming.
     * Note: The interval is the difference between today and the day the task is active.
     * this only works up to 7 days.
     *
     *
     * Please tweak the code if you want to make it work for more than 7 days.
     */
    private fun setOnClickOpenToPackInDetailUpcoming(it: TaskAndSessionJoin) {
        val todayIndex = DatetimeAppManager().getTodayDayId()
        val interval =
            if (todayIndex <= it.indexDay) it.indexDay - todayIndex
            else 7 + it.indexDay - todayIndex
        setOnClickOpenToPackInDetail(it, interval)
    }

    /**
     * Set on click open quiz in detail upcoming.
     * Note: The interval is the difference between today and the day the task is active.
     * this only works up to 7 days.
     *
     *
     * Please tweak the code if you want to make it work for more than 7 days.
     */
    private fun setOnClickOpenQuizInDetailUpcoming(it: TaskAndSessionJoin) {
        val todayIndex = DatetimeAppManager().getTodayDayId()
        val interval =
            if (todayIndex <= it.indexDay) it.indexDay - todayIndex
            else 7 + it.indexDay - todayIndex
        setOnClickOpenQuizInDetail(it, interval)
    }

    /**
     * Set on click open detail upcoming.
     * Note: The interval is the difference between today and the day the task is active.
     * this only works up to 7 days.
     *
     *
     * Please tweak the code if you want to make it work for more than 7 days.
     */
    private fun setOnClickOpenDetailUpcoming(it: TaskAndSessionJoin) {
        val todayIndex = DatetimeAppManager().getTodayDayId()
        val interval =
            if (todayIndex <= it.indexDay) it.indexDay - todayIndex
            else 7 + it.indexDay - todayIndex
        setOnClickOpenDetail(it, interval)
    }

    private fun setOnClickOpenToPackInDetail(taskAndSessionJoinTable: TaskAndSessionJoin, interval: Int = 0) {
        val destination = HomeFragmentDirections.actionHomeFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL_GO_TO_PACK,
            title = taskAndSessionJoinTable.title,
            id = taskAndSessionJoinTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime().plusDays(interval.toLong()))
        )
        findNavController().navigate(destination)
    }


    private fun setOnClickOpenQuizInDetail(taskAndSessionJoinTable: TaskAndSessionJoin, interval: Int = 0) {
        val destination = HomeFragmentDirections.actionHomeFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL_GO_TO_QUIZ,
            title = taskAndSessionJoinTable.title,
            id = taskAndSessionJoinTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime().plusDays(interval.toLong()))
        )
        findNavController().navigate(destination)
    }

    private fun setOnClickOpenDetail(taskAndSessionJoinTable: TaskAndSessionJoin, interval: Int = 0) {
        val destination = HomeFragmentDirections.actionHomeFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL,
            title = taskAndSessionJoinTable.title,
            id = taskAndSessionJoinTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime().plusDays(interval.toLong()))
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


        // NOTE: Testing
        androidDevelopmentAlarmTest()
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
                taskViewModel.getJoinSessionByIndexDay(dateDayOne.getTodayDayId(), dateDayOne.selectedDetailDatetimeISO)
            }
            val dayTwo = withContext(Dispatchers.IO) {
                taskViewModel.getJoinSessionByIndexDay(dateDayTwo.getTodayDayId(), dateDayTwo.selectedDetailDatetimeISO)
            }
            val dayThree = withContext(Dispatchers.IO) {
                taskViewModel.getJoinSessionByIndexDay(dateDayThree.getTodayDayId(), dateDayThree.selectedDetailDatetimeISO)
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
                val tasks = taskViewModel.getJoinSessionByIndexDay(today.getTodayDayId(), today.selectedDetailDatetimeISO)
                tasks
            }
            todayMainTaskAdapter.submitList(todayTask)

            todayTask.sortedWith(
                compareBy {
                    LocalTime.parse(it.sessionTimeEnd)
                        .isBefore(DatetimeAppManager().selectedDetailDatetimeISO.toLocalTime())
                }
            )
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