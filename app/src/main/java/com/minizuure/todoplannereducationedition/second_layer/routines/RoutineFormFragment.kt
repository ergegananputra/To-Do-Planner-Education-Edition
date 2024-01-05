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
import com.minizuure.todoplannereducationedition.recycler.adapter.TempSessionDetailAdapter
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class RoutineFormFragment : Fragment() {

    val args : RoutineFormFragmentArgs by navArgs()
    val routineFormViewModel : RoutineFormViewModel by activityViewModels()

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

    private fun onLongClickSessionItem(session: TempSession, button: MaterialButton) {
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            button.visibility = View.GONE
            Toast.makeText(requireContext(), "Delete ${session.title}", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                routineFormViewModel.deleteTempSession(session.id)
                tempSessionDetailAdapter.notifyItemRemoved(session.id)
            }

        }

        lifecycleScope.launch {
            this.launch(Dispatchers.IO) {
                delay(5000)
                this.launch(Dispatchers.Main) { button.visibility = View.GONE }
            }
        }
    }

    private fun onClickSessionItem(it: TempSession) {
        val destination = RoutineFormFragmentDirections.actionRoutineFormFragmentToSessionFormFragment(
            sessionId = it.id,
            newRoutine = args.routineId == 0,
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
        setupSaveButton()
        setupAddSessionButton()
        setupSessionRecyclerView()
        setupTitle()
        setupDatePicker()
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
        if (args.routineId == 0) {
            setupRecyclerForNewRoutine()
        } else {
            setupRecyclerForExistingRoutine()
        }
    }

    private fun setupRecyclerForExistingRoutine() {
        // TODO : setup recycler for existing routine
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
            newRoutine = args.routineId == 0,
            title = null,
            startTime = null,
            endTime = null,
            selectedDays = null,
        )

        binding.buttonAddSession.setOnClickListener {
            findNavController().navigate(destination)
        }
    }

    private fun setupSaveButton() {
        binding.buttonSaveRouteForm.setOnClickListener {
            if (args.routineId == 0) {
                createRoutine()
            } else {
                updateRoutine()
            }
        }
    }

    private fun updateRoutine() {
        //TODO: upload ke database
        Toast.makeText(requireContext(), "Not Implemented yet", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun createRoutine() {
        val title = binding.textInputLayoutRoutineTitle.editText?.text.toString().trim()
        val description = binding.textInputLayoutDescription.editText?.text.toString().trim()
        var startDate = binding.textInputLayoutStartDate.editText?.text.toString()
        var endDate = binding.textInputLayoutEndDate.editText?.text.toString()

        if(validateForm(title, description, startDate, endDate)) {
            return
        }

        lifecycleScope.launch {
            startDate = DatetimeAppManager().convertReadableDateToIso8601(startDate)
            endDate = DatetimeAppManager().convertReadableDateToIso8601(endDate)

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

    private fun validateForm(
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

        if (args.routineId == 0) {
            if (routineFormViewModel.tempSessions.value?.isEmpty() == true) {
                val msg = getString(R.string.error_msg_session_empty)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                return true
            }
        } else {
            // TODO: check if there is at least one session in edit mode
            Toast.makeText(requireContext(), "Not Implemented yet", Toast.LENGTH_SHORT).show()
        }

        return false
    }


}