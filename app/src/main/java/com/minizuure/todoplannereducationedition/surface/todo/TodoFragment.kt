package com.minizuure.todoplannereducationedition.surface.todo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils.isToday
import android.util.Log
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
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_PACK
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_QUIZ
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
 * - Quiz Filter
 * - To Pack Filter
 * - Community Filter
 */
class TodoFragment : Fragment() {
    private val binding by lazy { FragmentTodoBinding.inflate(layoutInflater) }
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var searchJob: Job? = null

    private val selectedDateMainTaskAdapter by lazy {
        MainTaskAdapter(
            scope = lifecycleScope,
            notesViewModel = noteViewModel,
            onClickOpenDetail = {setOnClickOpenTask(it, OPEN_DETAIL)},
            onClickOpenQuizInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_QUIZ)},
            onClickOpenToPackInDetail = {setOnClickOpenTask(it, OPEN_DETAIL_GO_TO_PACK)}
        )
    }

    private fun getSelectedDate() : ZonedDateTime {
        val selectedDate = binding.textInputLayoutDatetimeTodo.editText?.text.toString().trim()
        return DatetimeAppManager(selectedDate, true).selectedDetailDatetimeISO
    }

    private fun setOnClickOpenTask(taskAndSessionJoinTable: TaskAndSessionJoin, action : String) {
        val destination = TodoFragmentDirections.actionTodoFragmentToTaskManagementActivity(
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

    private fun setChipAllEvent() {
        val chipAllEvenIcon = binding.chipAllTodo.chipIcon

        binding.chipAllTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipAllTodo.chipIcon = null
                val searchQuery = binding.searchBarTodo.editText?.text.toString().trim()
                updateQuizAdapterWithResult(searchQuery)
                binding.containerDatetime.visibility = View.GONE
            } else {
                binding.chipAllTodo.chipIcon = chipAllEvenIcon
                val searchQuery = binding.searchBarTodo.editText?.text.toString().trim()

                if (searchQuery.isEmpty()) {
                    updateQuizAdapter(forceUpdate = true)
                    binding.containerDatetime.visibility = View.VISIBLE
                } else {
                    updateQuizAdapterWithResult(searchQuery)
                }
            }
        }
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

                selectedDateMainTaskAdapter.setShowsTheDate(true)
                binding.containerDatetime.visibility = View.GONE
                updateQuizAdapterWithResult(searchQuery)


            } else {
                selectedDateMainTaskAdapter.setShowsTheDate(false)
                binding.containerDatetime.visibility = View.VISIBLE
                updateQuizAdapter(forceUpdate = true)

                handler.postDelayed(clearFocus, delayInMillis)
            }
        }

        binding.searchBarTodo.setEndIconOnClickListener {
            binding.searchBarTodo.editText?.setText("")
            updateQuizAdapter(forceUpdate = true)

            binding.containerDatetime.visibility = View.VISIBLE
            it.clearFocus()
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
        searchJob?.cancel()
        lifecycleScope.launch {
            val selectedDate = getSelectedDate()
            val selectedDateTasks = withContext(Dispatchers.IO) {

                taskViewModel.getJoinSessionByIndexDay(
                    indexDay = DatetimeAppManager(selectedDate).getTodayDayId(),
                    selectedDateTime = selectedDate,
                    isToday = isToday(selectedDate.toInstant().toEpochMilli())
                )
            }

            selectedDateMainTaskAdapter.submitList(selectedDateTasks)

            if (forceUpdate) selectedDateMainTaskAdapter.notifyDataSetChanged()

        }
    }

    private fun updateQuizAdapterWithResult(searchQuery: String) {
        //TODO: FILTERING FOR QUIZ AND TO PACK
        searchJob?.cancel()
        searchJob = lifecycleScope.launch(Dispatchers.IO) {

            try {
                val isAllTime = binding.chipAllTodo.isChecked


                val notes = withContext(Dispatchers.IO) {
                    noteViewModel.note.getAll()
                }

                val result = mutableSetOf<TaskAndSessionJoin>()

                val todayDate = DatetimeAppManager().selectedDetailDatetimeISO

                withContext(Dispatchers.IO) {
                    taskViewModel.search(searchQuery, todayDate, 1)
                }.let { taskAndSessionJoin ->
                    result.addAll(taskAndSessionJoin)
                    selectedDateMainTaskAdapter.submitList(result.toMutableList())
                }

                if (isAllTime) {
                    for (notesTaskTable in notes) {

                        val date =
                            DatetimeAppManager(notesTaskTable.dateISO8601).selectedDetailDatetimeISO
                        val tempResult = withContext(Dispatchers.IO) {
                            taskViewModel.search(searchQuery, date, 1)
                        }

                        result.addAll(tempResult)
                        selectedDateMainTaskAdapter.submitList(result.toMutableList())
                    }
                } else {
                    for (notesTaskTable in notes) {

                        val date =
                            DatetimeAppManager(notesTaskTable.dateISO8601).selectedDetailDatetimeISO

                        if (date.isAfter(todayDate)) {
                            val tempResult = withContext(Dispatchers.IO) {
                                taskViewModel.search(searchQuery, date, 1)
                            }

                            result.addAll(tempResult)
                            selectedDateMainTaskAdapter.submitList(result.toMutableList())
                        }
                    }
                }

                if (result.isEmpty()) {
                    val unEfficientSearch = withContext(Dispatchers.IO) {
                        taskViewModel.search(searchQuery, todayDate, 2)
                    }
                    result.addAll(unEfficientSearch)
                }

                Log.i("TodoFragment", "total result : ${result.size}")
                selectedDateMainTaskAdapter.submitList(result.toMutableList())
            } catch (e: CancellationException) {
                Log.i("TodoFragment", "searchJob cancelled")
            }
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