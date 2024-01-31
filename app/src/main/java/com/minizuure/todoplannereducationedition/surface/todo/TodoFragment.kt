package com.minizuure.todoplannereducationedition.surface.todo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils.isToday
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
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
import java.time.ZonedDateTime

/**
 * TodoFragment adalah tempat untuk mencari task dan menambahkan task baru.
 * Pada TodoFragment, user dapat melihat task pada tanggal tertentu.
 *
 *
 * Status : Belum Selesai, MVP Done
 *
 *
 * Development TODO List :
 * - Search bar & Result
 * - Quiz Filter
 * - To Pack Filter
 * - All Event Filter
 * - Community Filter
 */
class TodoFragment : Fragment() {
    private val binding by lazy { FragmentTodoBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val selectedDateMainTaskAdapter by lazy {
        MainTaskAdapter(
            currentDate = getSelectedDate(),
            scope = lifecycleScope,
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

    private fun setOnClickOpenToPackInDetail(taskAndSessionJoin: TaskAndSessionJoin) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = TaskManagementActivity.OPEN_DETAIL_GO_TO_PACK,
            title = taskAndSessionJoin.title,
            id = taskAndSessionJoin.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate())
        )
        findNavController().navigate(destination)
    }

    private fun setOnClickOpenQuizInDetail(taskAndSessionJoin: TaskAndSessionJoin) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = TaskManagementActivity.OPEN_DETAIL_GO_TO_QUIZ,
            title = taskAndSessionJoin.title,
            id = taskAndSessionJoin.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate()),
        )
        findNavController().navigate(destination)
    }

    private fun setOnClickOpenDetail(taskAndSessionJoin: TaskAndSessionJoin) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
            actionToOpen = OPEN_DETAIL,
            title = taskAndSessionJoin.title,
            id = taskAndSessionJoin.id,
            selectedDatetimeISO = ParcelableZoneDateTime(getSelectedDate()),
            indexDay = taskAndSessionJoin.indexDay,
            sessionId = taskAndSessionJoin.fkSessionId
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

        setupSearchBar()
        setupChipFilter()
    }

    private fun setupChipFilter() {
        setChipAllEvent()
        setChipCommunity()
    }

    private fun setChipCommunity() {
        val chipCommunityIcon = binding.chipCommunitiesTodo.chipIcon

        binding.chipCommunitiesTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipCommunitiesTodo.chipIcon = null
            } else {
                binding.chipCommunitiesTodo.chipIcon = chipCommunityIcon
            }
        }
    }

    private fun setChipAllEvent() {
        val chipAllEvenIcon = binding.chipAllTodo.chipIcon

        binding.chipAllTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipAllTodo.chipIcon = null
            } else {
                binding.chipAllTodo.chipIcon = chipAllEvenIcon
            }
        }
    }

    private fun setupSearchBar() {
        val handler = Handler(Looper.getMainLooper())
        val delayInMillis = 5_000L
        val clearFocus = Runnable {
            binding.searchBarTodo.clearFocus()
        }

        binding.searchBarTodo.editText?.doAfterTextChanged { text ->
            val searchQuery = text.toString().trim()

            if (searchQuery.isNotEmpty()) {
                handler.removeCallbacks(clearFocus)

                binding.containerDatetime.visibility = View.GONE
                updateQuizAdapterWithResult(searchQuery)


            } else {
                binding.containerDatetime.visibility = View.VISIBLE
                updateQuizAdapter(forceUpdate = true)

                handler.postDelayed(clearFocus, delayInMillis)
            }
        }

        binding.searchBarTodo.setEndIconOnClickListener {
            binding.searchBarTodo.editText?.setText("")
            updateQuizAdapter(forceUpdate = true)

            binding.containerDatetime.visibility = View.VISIBLE
        }
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

    private fun updateQuizAdapter(forceUpdate: Boolean = false) {
        lifecycleScope.launch {
            val selectedDate = getSelectedDate()
            val selectedDateTasks = withContext(Dispatchers.IO) {

                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = DatetimeAppManager(selectedDate).getTodayDayId(),
                    selectedDateTime = selectedDate,
                    isToday = isToday(selectedDate.toInstant().toEpochMilli())
                )
            }

            selectedDateMainTaskAdapter.setNewCurrentDate(selectedDate)
            selectedDateMainTaskAdapter.submitList(selectedDateTasks)

            if (forceUpdate) selectedDateMainTaskAdapter.notifyDataSetChanged()

        }
    }

    private fun updateQuizAdapterWithResult(searchQuery: String) {
        //TODO: Search result from database
        lifecycleScope.launch {
            val searchResult = withContext(Dispatchers.IO) {
                taskViewModel.search(searchQuery)
            }

            val selectedDate = ZonedDateTime.now()
                .withYear(1970)
                .withMonth(1)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)

            selectedDateMainTaskAdapter.setNewCurrentDate(selectedDate)
            selectedDateMainTaskAdapter.submitList(searchResult)
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
        updateQuizAdapter(forceUpdate = true)
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