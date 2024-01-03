package com.minizuure.todoplannereducationedition.surface.todo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentTodoBinding
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailActivity


class TodoFragment : Fragment() {
    private val binding by lazy { FragmentTodoBinding.inflate(layoutInflater) }

    private val launcherToAddTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("TodoFragment", "onCreate: RESULT_OK")
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
            Toast.makeText(requireContext(), "TODO: add task clicked", Toast.LENGTH_SHORT).show()
//            val intentToTaskForm = Intent(requireActivity(), xx::class.java)
//            launcherToAddTask.launch(intentToTaskForm)
        }
    }

}