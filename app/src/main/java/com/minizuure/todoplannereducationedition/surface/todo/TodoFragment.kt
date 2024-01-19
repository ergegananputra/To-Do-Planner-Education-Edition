package com.minizuure.todoplannereducationedition.surface.todo

import android.os.Bundle
import android.text.format.DateUtils.isToday
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentTodoBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZonedDateTime


class TodoFragment : Fragment() {
    private val binding by lazy { FragmentTodoBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val selectedDateMainTaskAdapter by lazy {
        MainTaskAdapter(
            currentDate = DatetimeAppManager().selectedDetailDatetimeISO,
            scope = lifecycleScope,
            sessionViewModel = sessionViewModel,
            routineViewModel = routineViewModel,
            taskViewModel = taskViewModel,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenDetail(it)},
            onClickOpenQuizInDetail = {setOnClickOpenQuizInDetail(it)},
            onClickOpenToPackInDetail = {setOnClickOpenToPackInDetail(it)}
        )
    }

    private fun getSelectedDate() : ZonedDateTime {
        val selectedDate = binding.textInputLayoutDatetimeTodo.editText?.text.toString().trim()
        return DatetimeAppManager(selectedDate, true).selectedDetailDatetimeISO
    }

    private fun setOnClickOpenToPackInDetail(taskTable: TaskTable) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = TaskManagementActivity.OPEN_DETAIL_GO_TO_PACK,
            title = taskTable.title,
            id = taskTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate())
        )
        findNavController().navigate(destination)
    }

    private fun setOnClickOpenQuizInDetail(taskTable: TaskTable) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = TaskManagementActivity.OPEN_DETAIL_GO_TO_QUIZ,
            title = taskTable.title,
            id = taskTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate()),
        )
        findNavController().navigate(destination)
    }

    private fun setOnClickOpenDetail(taskTable: TaskTable) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL,
            title = taskTable.title,
            id = taskTable.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate())
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

        setupCalendarDialog()
        setupDateArrowButtons()
        setupRecyclerView()
        setupEfabAddTask()
    }

    private fun setupDateArrowButtons() {
        binding.buttonDatetimeArrowLeft.setOnClickListener {
            val currentSelected = getSelectedDate()
            val newSelected = currentSelected.minusDays(1)

            binding.textInputLayoutDatetimeTodo.editText?.setText(DatetimeAppManager(newSelected).toReadable())

            updateQuizAdapter()
        }

        binding.buttonDatetimeArrowRight.setOnClickListener {
            val currentSelected = getSelectedDate()
            val newSelected = currentSelected.plusDays(1)

            binding.textInputLayoutDatetimeTodo.editText?.setText(DatetimeAppManager(newSelected).toReadable())

            updateQuizAdapter()
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

    private fun setupRecyclerView() {
        binding.recyclerViewTodo.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = selectedDateMainTaskAdapter
        }

        updateQuizAdapter()
    }

    private fun updateQuizAdapter() {
        lifecycleScope.launch {
            val selectedDateTasks = withContext(Dispatchers.IO) {
                val selectedDate = getSelectedDate()
                val tasks = taskViewModel.getByIndexDay(DatetimeAppManager(selectedDate).getTodayDayId(), selectedDate)

                selectedDateMainTaskAdapter.submitList(tasks)
                val session = sessionViewModel.getAll()

                if (isToday(selectedDate.toInstant().toEpochMilli())) {
                    tasks.sortedWith(
                        compareBy {
                            LocalTime.parse(session.first { session -> session.id == it.sessionId }.timeEnd)
                                .isBefore(DatetimeAppManager().selectedDetailDatetimeISO.toLocalTime())
                        }
                    )
                }

                tasks
            }

            selectedDateMainTaskAdapter.submitList(selectedDateTasks)



        }
    }

    private fun setupCalendarDialog() {
        DatetimeAppManager().setEditTextDatePickerDialog(
            context = requireContext(),
            parentFragmentManager = parentFragmentManager,
            textInputLayoutDate = binding.textInputLayoutDatetimeTodo,
            customSuccessAction = setOnSuccessDatetimePickerDialog()
        )

        val today = DatetimeAppManager().toReadable()
        binding.textInputLayoutDatetimeTodo.editText?.setText(today)
    }

    private fun setOnSuccessDatetimePickerDialog(): () -> Unit = {
        updateQuizAdapter()
    }

    private fun setupEfabAddTask() {
        binding.efabAddTask.setOnClickListener {
            val destination = TodoFragmentDirections
                .actionTodoFragmentToTaskManagementActivity(
                    actionToOpen = TaskManagementActivity.OPEN_TASK,
                    title = null,
                    selectedDatetimeISO = ParcelableZoneDateTime(DatetimeAppManager().getLocalDateTime())
                )
            findNavController().navigate(destination)
        }


        binding.nestedScrollViewTodo.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.efabAddTask.shrink()
            } else {
                binding.efabAddTask.extend()
            }
        }
    }

}