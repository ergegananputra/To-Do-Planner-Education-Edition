package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutinesBinding
import com.minizuure.todoplannereducationedition.recycler.adapter.RoutinesAdapter
import com.minizuure.todoplannereducationedition.recycler.model.RoutinesItemPreview
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutinesFragment : Fragment() {
    private val routineFormViewModel : RoutineFormViewModel by activityViewModels()
    private val binding by lazy {
        FragmentRoutinesBinding.inflate(layoutInflater)
    }

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private val routineAdapter by lazy {
        RoutinesAdapter(
            routines = mutableListOf(),
            onClick = {
                onClickRoutineItem(it.id)
            },
            onClickDelete = {
                onClickDeleteRoutineItem(it.id)
            }
        )
    }

    private fun onClickDeleteRoutineItem(id: Long) {
        lifecycleScope.launch {
            val index = routineAdapter.getIndexById(id)
            val deletedRoutine = routineViewModel.getById(id)
            if (deletedRoutine != null && index != -1) {
                routineViewModel.delete(deletedRoutine)
                routineAdapter.notifyItemRemoved(index)
                Toast.makeText(requireContext(), "Deleted ${deletedRoutine.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun onClickRoutineItem(id: Long) {
        // TODO: Open Detail
        Toast.makeText(requireContext(), "Clicked $id", Toast.LENGTH_SHORT).show()
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

        setupEfabCreateRoutine()
        setupRecyclerView()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.recyclerViewRoutines.apply {
            adapter = routineAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        updateRecyclerViewData()
    }

    private fun updateRecyclerViewData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val routines = routineViewModel.getAll()
            val routinesItemPreview = mutableListOf<RoutinesItemPreview>()
            routines.forEach {
                val count = sessionViewModel.countSessionsForRoutine(it.id)
                routinesItemPreview.add(RoutinesItemPreview(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    totalUsed = count,
                ))
            }
            routineAdapter.submitList(routinesItemPreview.toMutableList())
        }
    }

    private fun setupEfabCreateRoutine() {
        binding.efabCreateNewRoutine.setOnClickListener {
            val destination = RoutinesFragmentDirections.actionRoutinesFragmentToRoutineFormFragment(0)
            lifecycleScope.launch {
                routineFormViewModel.clearTempSessions()
                findNavController().navigate(destination)
            }

        }
    }


}