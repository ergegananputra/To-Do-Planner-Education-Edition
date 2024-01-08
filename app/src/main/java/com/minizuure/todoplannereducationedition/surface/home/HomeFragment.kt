package com.minizuure.todoplannereducationedition.surface.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.databinding.FragmentHomeBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_TASK


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }


    private val launcherToAddTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("HomeFragment", "onCreate: RESULT_OK")
            //TODO: do something
        }
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

        setupEfabAddTask()
    }

    private fun setupEfabAddTask() {
        binding.efabAddTask.setOnClickListener {
//            val intentToTaskForm = Intent(requireActivity(), TaskManagementActivity::class.java)
//            intentToTaskForm.putExtra(EXTRA_OPEN_TASK, true)
//            launcherToAddTask.launch(intentToTaskForm)
            val destination = HomeFragmentDirections
                .actionHomeFragmentToTaskManagementActivity(actionToOpen = OPEN_TASK, title = null)
            findNavController().navigate(destination)
        }
    }


}