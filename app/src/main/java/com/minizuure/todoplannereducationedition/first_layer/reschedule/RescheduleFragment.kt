package com.minizuure.todoplannereducationedition.first_layer.reschedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentRescheduleBinding
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager


class RescheduleFragment : Fragment() {

    private val binding by lazy {
        FragmentRescheduleBinding.inflate(layoutInflater)
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
        setErrorMessages()
    }

    private fun setupStartTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.rescheduleInputStartSession
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.rescheduleInputEndSession
        )
    }

    private fun setErrorMessages() {
        ErrorMsgManager().setErrorMessages(
            requireContext(),
            binding.rescheduleInputLayoutLocation,
            binding.rescheduleInputEditTextLocation,
            "20 characters max"
        )
    }
}