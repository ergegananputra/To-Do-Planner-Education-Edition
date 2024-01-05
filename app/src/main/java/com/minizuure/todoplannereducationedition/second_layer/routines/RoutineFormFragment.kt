package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutineFormBinding
import com.minizuure.todoplannereducationedition.model.TempSession
import com.minizuure.todoplannereducationedition.recycler.adapter.SessionDetailAdapter
import com.minizuure.todoplannereducationedition.recycler.adapter.TempSessionDetailAdapter
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity.Companion.DEFAULT_ROUTINE_ID
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RoutineFormFragment : Fragment() {

    val args : RoutineFormFragmentArgs by navArgs()
    private val routineFormViewModel : RoutineFormViewModel by activityViewModels()

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private val binding by lazy {
        FragmentRoutineFormBinding.inflate(layoutInflater)
    }

    private val tempSessionDetailAdapter by lazy {
        TempSessionDetailAdapter(
            sessions = mutableListOf(),
            onClick = {
                onClickSessionItem(it)
            },
            onLongClick = { session, button ->
                onLongClickSessionItem(session, button)
            },
        )
    }

    private val sessionDetailAdapter by lazy {
        SessionDetailAdapter(
            onClick = {
                onClickSessionItem(it)
            },
            onLongClick = { sessionTable, button ->
                onLongClickSessionItem(sessionTable, button)
            },
        )
    }

    private fun onLongClickSessionItem(session: SessionTable, button: MaterialButton) {
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            button.visibility = View.GONE
            lifecycleScope.launch {
                val index = withContext(Dispatchers.IO) { sessionDetailAdapter.getIndexById(session.id) }
                val sessionToDelete =  withContext(Dispatchers.IO) { sessionViewModel.getById(session.id) }
                sessionToDelete?.let {
                    Toast.makeText(requireContext(), "Delete ${session.title}", Toast.LENGTH_SHORT).show()
                    sessionViewModel.delete(sessionToDelete)
                    sessionDetailAdapter.removeIndex(index)

                    updateRecyclerViewData()
                }
            }

        }

        lifecycleScope.launch {
            this.launch(Dispatchers.IO) {
                delay(5000)
                this.launch(Dispatchers.Main) { button.visibility = View.GONE }
            }
        }
    }

    private fun onLongClickSessionItem(session: TempSession, button: MaterialButton) {
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            button.visibility = View.GONE
            Toast.makeText(requireContext(), "Delete ${session.title}", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                routineFormViewModel.deleteTempSession(session.id)
                tempSessionDetailAdapter.notifyItemRemoved(session.id.toInt())
            }

        }

        lifecycleScope.launch {
            this.launch(Dispatchers.IO) {
                delay(5000)
                this.launch(Dispatchers.Main) { button.visibility = View.GONE }
            }
        }
    }

    private fun onClickSessionItem(it: SessionTable) {
        val destination = RoutineFormFragmentDirections.actionRoutineFormFragmentToSessionFormFragment(
            sessionId = it.id,
            newRoutine = args.routineId == DEFAULT_ROUTINE_ID,
            routineId = it.fkRoutineId,
            title = it.title,
            startTime = it.timeStart,
            endTime = it.timeEnd,
            selectedDays = it.selectedDays
        )
        findNavController().navigate(destination)
    }

    private fun onClickSessionItem(it: TempSession) {
        val destination = RoutineFormFragmentDirections.actionRoutineFormFragmentToSessionFormFragment(
            sessionId = it.id,
            newRoutine = args.routineId == DEFAULT_ROUTINE_ID,
            title = it.title,
            startTime = it.startTime,
            endTime = it.endTime,
            selectedDays = it.daysSelected
        )
        findNavController().navigate(destination)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as RoutineManagementActivity).setToolbarTitle(this)
        setupViewModelFactory()

        if (args.routineId != DEFAULT_ROUTINE_ID) {
            loadData()
        }

        setupAddSessionButton()
        setupSessionRecyclerView()
        setupTitle()
        setupDatePicker()

        setupSaveButton()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val routine = withContext(Dispatchers.IO) {routineViewModel.getById(args.routineId)}
            if (routine != null) {
                val readableStartDate = DatetimeAppManager().convertIso8601ToReadableDate(routine.date_start)
                val readableEndDate = DatetimeAppManager().convertIso8601ToReadableDate(routine.date_end)

                binding.textInputLayoutRoutineTitle.editText?.setText(routine.title)
                binding.textInputLayoutDescription.editText?.setText(routine.description)
                binding.textInputLayoutStartDate.editText?.setText(readableStartDate)
                binding.textInputLayoutEndDate.editText?.setText(readableEndDate)
            }
        }
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = requireActivity().application as ToDoPlannerApplication

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]
    }

    private fun setupTitle() {
        binding.textInputLayoutRoutineTitle.editText?.doAfterTextChanged { text ->
            if (text.toString().trim().isNotEmpty()) {
                binding.textInputLayoutRoutineTitle.error = null
            }
        }
    }

    private fun setupDatePicker() {
        DatetimeAppManager().setEditTextDatePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartDate,
            title = getString(R.string.select_start_date)
        )

        DatetimeAppManager().setEditTextDatePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndDate,
            title = getString(R.string.select_end_date)
        )
    }

    private fun setupSessionRecyclerView() {
        if (args.routineId == DEFAULT_ROUTINE_ID) {
            setupRecyclerForNewRoutine()
        } else {
            setupRecyclerForExistingRoutine()
        }
    }

    private fun setupRecyclerForExistingRoutine() {
        binding.recyclerViewSessionsForm.apply {
            adapter = sessionDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        updateRecyclerViewData()
    }

    private fun updateRecyclerViewData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sessions = sessionViewModel.getByRoutineId(args.routineId)
            val sessionsItemPreview = mutableListOf<SessionTable>()
            sessions.forEach {
                sessionsItemPreview.add(it)
            }
            sessionDetailAdapter.submitList(sessionsItemPreview.toMutableList())
        }
    }

    private fun setupRecyclerForNewRoutine() {
        binding.recyclerViewSessionsForm.apply {
            adapter = tempSessionDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        routineFormViewModel.tempSessions.observe(viewLifecycleOwner) {
            tempSessionDetailAdapter.sessions = it
            tempSessionDetailAdapter.notifyDataSetChanged()
        }
    }

    private fun setupAddSessionButton() {
        val destination = RoutineFormFragmentDirections.actionRoutineFormFragmentToSessionFormFragment(
            newRoutine = args.routineId == DEFAULT_ROUTINE_ID,
            routineId = args.routineId,
            title = null,
            startTime = null,
            endTime = null,
            selectedDays = null,
        )

        binding.buttonAddSession.setOnClickListener {
            clearEditTextFocus()
            findNavController().navigate(destination)
        }
    }

    private fun setupSaveButton() {
        binding.buttonSaveRouteForm.setOnClickListener {
            clearEditTextFocus()
            val title = binding.textInputLayoutRoutineTitle.editText?.text.toString().trim()
            val description = binding.textInputLayoutDescription.editText?.text.toString().trim()
            val startDate = binding.textInputLayoutStartDate.editText?.text.toString()
            val endDate = binding.textInputLayoutEndDate.editText?.text.toString()

            lifecycleScope.launch {
                if (args.routineId == DEFAULT_ROUTINE_ID) {
                    createRoutine(title, description, startDate, endDate)
                } else {
                    updateRoutine(title, description, startDate, endDate)
                }
            }

        }
    }

    private fun clearEditTextFocus() {
        with(binding) {
            textInputLayoutRoutineTitle.clearFocus()
            textInputLayoutDescription.clearFocus()
            textInputLayoutStartDate.clearFocus()
            textInputLayoutEndDate.clearFocus()
        }
    }

    private suspend fun updateRoutine(
        title: String,
        description: String,
        startDateArg: String,
        endDateArg: String
    ) {
        if(validateForm(title, description, startDateArg, endDateArg)) {
            return
        }
        lifecycleScope.launch {
            val startDate = DatetimeAppManager().convertReadableDateToIso8601(startDateArg)
            val endDate = DatetimeAppManager().convertReadableDateToIso8601(endDateArg)

            this.launch(Dispatchers.IO) {
                routineViewModel.update(
                    id = args.routineId,
                    title = title,
                    description = description,
                    dateStart = startDate,
                    dateEnd = endDate,
                )
            }

            findNavController().navigateUp()
        }
    }

    private suspend fun createRoutine(
        title: String,
        description: String,
        startDateArg: String,
        endDateArg: String
    ) {
        if(validateForm(title, description, startDateArg, endDateArg)) {
            return
        }

        lifecycleScope.launch {
            val startDate = DatetimeAppManager().convertReadableDateToIso8601(startDateArg)
            val endDate = DatetimeAppManager().convertReadableDateToIso8601(endDateArg)

            val routineId = routineViewModel.insert(
                title = title,
                description = description,
                dateStart = startDate,
                dateEnd = endDate,
            )

           routineFormViewModel.tempSessions.asFlow().first().forEach {
               sessionViewModel.insert(
                   title = it.title,
                   timeStart = it.startTime,
                   timeEnd = it.endTime,
                   selectedDays = it.daysSelected,
                   fkRoutineId = routineId
               )
           }

            findNavController().navigateUp()
        }
    }

    private suspend fun validateForm(
        title: String,
        description: String,
        startDate: String,
        endDate: String
    ) : Boolean {
        if (title.isEmpty()) {
            binding.textInputLayoutRoutineTitle.error = getString(R.string.error_msg_title_empty)
            return true
        }
        if (startDate.isEmpty()) {
            binding.textInputLayoutStartDate.error = getString(R.string.error_msg_start_date_empty)
            return true
        }
        if (endDate.isEmpty()) {
            binding.textInputLayoutEndDate.error = getString(R.string.error_msg_end_date_empty)
            return true
        }

        if (args.routineId == DEFAULT_ROUTINE_ID) {
            if (routineFormViewModel.tempSessions.value?.isEmpty() == true) {
                val msg = getString(R.string.error_msg_session_empty)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                return true
            }
        } else {
            val sessions = withContext(Dispatchers.IO) { sessionViewModel.getByRoutineId(args.routineId) }
            if (sessions.isEmpty()) {
                val msg = getString(R.string.error_msg_session_empty)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                return true
            }

        }

        return false
    }


}