package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutineFormBinding
import com.minizuure.todoplannereducationedition.model.TempSession
import com.minizuure.todoplannereducationedition.recycler.adapter.TempSessionDetailAdapter
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class RoutineFormFragment : Fragment() {

    val args : RoutineFormFragmentArgs by navArgs()
    val routineFormViewModel : RoutineFormViewModel by activityViewModels()

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

        setupSaveButton()
        setupAddSessionButton()
        setupSessionRecyclerView()
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
            //TODO: upload ke database
            Toast.makeText(requireContext(), "Save routine", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }


}