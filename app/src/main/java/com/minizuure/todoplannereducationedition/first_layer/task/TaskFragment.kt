package com.minizuure.todoplannereducationedition.first_layer.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentTaskBinding
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager

class TaskFragment : Fragment() {

    private val binding by lazy {
        FragmentTaskBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStartTimePicker()
        setupErrorMessages()
    }
    private fun setupStartTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.taskInputStartSession
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.taskInputEndSession
        )
    }

    private fun setupErrorMessages() {
        ErrorMsgManager().setErrorMessages(
            requireContext(),
            binding.taskInputLayoutLocation,
            binding.taskInputEditTextLocation,
            "20 characters max"
        )
    }
}