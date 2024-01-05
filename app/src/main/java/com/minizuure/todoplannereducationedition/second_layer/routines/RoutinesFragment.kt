package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutinesBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import kotlinx.coroutines.launch

class RoutinesFragment : Fragment() {
    private val routineFormViewModel : RoutineFormViewModel by activityViewModels()
    private val binding by lazy {
        FragmentRoutinesBinding.inflate(layoutInflater)
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

        setupEfabCreateRoutine()
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