package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutineDetailBinding
import com.minizuure.todoplannereducationedition.recycler.adapter.SessionDetailAdapter
import com.minizuure.todoplannereducationedition.recycler.adapter.UsagesAdapter
import com.minizuure.todoplannereducationedition.recycler.model.UsagesPreviewModel
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RoutineDetailFragment : Fragment() {

    private val args : RoutineDetailFragmentArgs by navArgs()
    private val binding by lazy {
        FragmentRoutineDetailBinding.inflate(layoutInflater)
    }

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel

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

    private val usagesAdapter by lazy {
        UsagesAdapter(
            context = requireContext(),
            onClick = {
                onClickUsagesItem(it)
            }
        )
    }

    private fun onClickUsagesItem(it: UsagesPreviewModel) {
        // TODO : Create usages detail fragment and navigate to it

        Toast.makeText(requireContext(), "Clicked ${it.name} [Not implemented yet]", Toast.LENGTH_SHORT).show()
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

                    updateRecyclerViewSessionData()
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

    private fun updateRecyclerViewSessionData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sessions = sessionViewModel.getByRoutineId(args.routineId)
            val sessionsItemPreview = mutableListOf<SessionTable>()
            sessions.forEach {
                sessionsItemPreview.add(it)
            }
            sessionDetailAdapter.submitList(sessionsItemPreview.toMutableList())
        }
    }

    private fun updateRecyclerViewUsagesData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val usages = mutableListOf<UsagesPreviewModel>()
            DatetimeAppManager().getAllDaysOfWeek().forEachIndexed { index, name ->

                // TODO : Count used for each usage
                val used = 0

                val usage = UsagesPreviewModel(
                    id = index,
                    name = name,
                    used = 0
                )

                usages.add(usage)
            }

            usagesAdapter.submitList(usages)
        }
    }

    private fun onClickSessionItem(it: SessionTable) {
        val destination = RoutineDetailFragmentDirections.actionRoutineDetailFragmentToSessionFormFragment(
            sessionId = it.id,
            newRoutine = args.routineId == RoutineManagementActivity.DEFAULT_ROUTINE_ID,
            routineId = it.fkRoutineId,
            title = it.title,
            startTime = it.timeStart,
            endTime = it.timeEnd,
            selectedDays = it.selectedDays
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

        setupTextViews()
        setupSessionRecyclerView()
        setupAddSessionButton()
        setupUsagesRecyclerView()

        checkCommunity()

    }

    private fun checkCommunity() {
        lifecycleScope.launch(Dispatchers.IO) {
            val routine = routineViewModel.getById(args.routineId)
            routine?.let {
                if (routine.isSharedToCommunity) {
                    binding.imageViewIconCommunity.visibility = View.VISIBLE
                } else {
                    binding.imageViewIconCommunity.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setupTextViews() {
        binding.textViewTitleRoutineDetail.text = args.title
        binding.textViewDescriptionRoutineDetail.text = args.description

        expiredDate()
    }

    private fun expiredDate() {
        lifecycleScope.launch(Dispatchers.IO) {
            val routine = routineViewModel.getById(args.routineId)
            routine?.let {
                val readableDate = DatetimeAppManager().convertIso8601ToReadableDate(it.date_end)
                val textExpired = "${getString(R.string.expired_in)} $readableDate"
                withContext(Dispatchers.Main) {
                    binding.textViewExpiredDateRoutineDetail.text = textExpired
                }
            }
        }
    }

    private fun setupUsagesRecyclerView() {
        binding.recyclerViewUsagesRoutineDetail.apply {
            adapter = usagesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        updateRecyclerViewUsagesData()
    }

    private fun setupSessionRecyclerView() {
        binding.recyclerViewSessionRoutineDetail.apply {
            adapter = sessionDetailAdapter
            layoutManager = LinearLayoutManager(context)
        }

        updateRecyclerViewSessionData()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]
    }

    private fun setupAddSessionButton() {
        val destination = RoutineDetailFragmentDirections.actionRoutineDetailFragmentToSessionFormFragment(
            newRoutine = args.routineId == RoutineManagementActivity.DEFAULT_ROUTINE_ID,
            routineId = args.routineId,
            title = null,
            startTime = null,
            endTime = null,
            selectedDays = null,
        )

        binding.buttonAddSessionRoutineDetail.setOnClickListener {
            findNavController().navigate(destination)
        }
    }
}