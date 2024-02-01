package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutinesBinding
import com.minizuure.todoplannereducationedition.recycler.adapter.RoutinesAdapter
import com.minizuure.todoplannereducationedition.recycler.model.RoutinesItemPreview
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_ROUTINE_ID
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoutinesFragment : Fragment() {
    private val routineFormViewModel : RoutineFormViewModel by activityViewModels()
    private val binding by lazy {
        FragmentRoutinesBinding.inflate(layoutInflater)
    }

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private val routineAdapter by lazy {
        RoutinesAdapter(
            onClick = {
                onClickRoutineItem(it.id, it.title, it.description)
            },
            onClickDelete = {
                onClickDeleteRoutineItem(it.id)
            },
            onLongClick = {
                onClickLongRoutineItem(it.id)
            },
            onClickSelect = {
                onClickSelectRoutineItem(it.id, it.title)
            },
        )
    }

    private fun onClickSelectRoutineItem(id: Long, title: String) {
        UserPreferences(requireContext()).apply {
            defaultRoutineId = id
            defaultRoutineName = title
        }
        cancelDefaultRoutineMode()
        binding.textViewStatusDefaultRoutineName.text = title
    }

    private fun onClickLongRoutineItem(id: Long) {
        val destination = RoutinesFragmentDirections.actionRoutinesFragmentToRoutineFormFragment(id)
        findNavController().navigate(destination)
    }

    private fun onClickDeleteRoutineItem(id: Long) {
        lifecycleScope.launch {
            val index = routineAdapter.getIndexById(id)
            val deletedRoutine = routineViewModel.getById(id)
            if (deletedRoutine != null && index != -1) {
                routineViewModel.delete(deletedRoutine)
                routineAdapter.notifyItemRemoved(index)
                Toast.makeText(requireContext(), "Deleted ${deletedRoutine.title}", Toast.LENGTH_SHORT).show()

                updateRecyclerViewData()
            }
        }
    }


    private fun onClickRoutineItem(id: Long, title: String, description: String) {
        val destination = RoutinesFragmentDirections.actionRoutinesFragmentToRoutineDetailFragment(
            routineId = id,
            title = title,
            description = description
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

        setupEfabCreateRoutine()
        setupRecyclerView()
        setupTotalRoutineCounter()
        setupDefaultRoutine()
        setupSearchBar()
    }

    private fun setupSearchBar() {
        binding.searchBarRoutines.editText?.doAfterTextChanged {
            val query = it.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val routines = routineViewModel.getPaginated(routineViewModel.getCount(), search = query)
                val routinesItemPreview = mutableListOf<RoutinesItemPreview>()
                routines.forEach {routine ->
                    val count = sessionViewModel.countSessionsForRoutine(routine.id)
                    routinesItemPreview.add(RoutinesItemPreview(
                        id = routine.id,
                        title = routine.title,
                        description = routine.description,
                        totalUsed = count,
                    ))
                }
                routineAdapter.submitList(routinesItemPreview.toMutableList())
            }
        }

        binding.searchBarRoutines.setEndIconOnClickListener {
            binding.searchBarRoutines.clearFocus()
            updateRecyclerViewData()
        }
    }

    private fun setupDefaultRoutine() {
        lifecycleScope.launch {
            val routineName = UserPreferences(requireContext()).defaultRoutineName
            binding.textViewStatusDefaultRoutineName.text = routineName
        }

        binding.cardViewDefaultRoutine.setOnClickListener {
            if (!routineAdapter.isSelectMode) {
                selectDefaultRoutineMode()
            }
        }

        binding.buttonDefaultRoutine.setOnClickListener {
            selectDefaultRoutineMode()
        }

        binding.buttonCancelDefaultRoutine.setOnClickListener {
            cancelDefaultRoutineMode()
        }
    }

    private fun cancelDefaultRoutineMode() {
        binding.textViewChooseRoutine.visibility = View.GONE

        val textDefaultRoutine = getText(R.string.set_default_routine)
        binding.textViewTitleDefaultRoutine.text = textDefaultRoutine

        binding.textViewStatusDefaultRoutineName.visibility = View.VISIBLE

        binding.buttonDefaultRoutine.visibility = View.VISIBLE
        binding.buttonCancelDefaultRoutine.visibility = View.GONE
        routineAdapter.isSelectMode = false
    }

    private fun selectDefaultRoutineMode() {
        binding.textViewChooseRoutine.visibility = View.VISIBLE

        val textNoRoutineSelected = getText(R.string.no_routine_selected)
        binding.textViewTitleDefaultRoutine.text = textNoRoutineSelected

        binding.textViewStatusDefaultRoutineName.visibility = View.GONE

        binding.buttonDefaultRoutine.visibility = View.GONE
        binding.buttonCancelDefaultRoutine.visibility = View.VISIBLE
        routineAdapter.isSelectMode = true
    }

    private fun setupTotalRoutineCounter() {
        lifecycleScope.launch(Dispatchers.Main) {
            val count = withContext(Dispatchers.IO) { routineViewModel.getCount() }
            binding.textViewRoutineSum.text = count.toString()
        }

        routineAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.textViewRoutineSum.text = routineAdapter.itemCount.toString()
            }
        })
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
            val destination = RoutinesFragmentDirections.actionRoutinesFragmentToRoutineFormFragment(DEFAULT_ROUTINE_ID)
            lifecycleScope.launch {
                routineFormViewModel.clearTempSessions()
                findNavController().navigate(destination)
            }

        }
    }


}