package com.minizuure.todoplannereducationedition.second_layer.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.databinding.FragmentSessionFormBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

class SessionFormFragment : Fragment() {
    val args : SessionFormFragmentArgs by navArgs()
    private val binding by lazy {
        FragmentSessionFormBinding.inflate(layoutInflater)
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
        setupStartTimePicker()
        setupDaysTag()
    }

    private fun setupDaysTag() {
        with(binding) {
            buttonTagsSunday.setOnClickListener {
                buttonTagsSunday.isActivated = !buttonTagsSunday.isActivated
            }
            buttonTagsMonday.setOnClickListener {
                buttonTagsMonday.isActivated = !buttonTagsMonday.isActivated
            }
            buttonTagsTuesday.setOnClickListener {
                buttonTagsTuesday.isActivated = !buttonTagsTuesday.isActivated
            }
            buttonTagsWednesday.setOnClickListener {
                buttonTagsWednesday.isActivated = !buttonTagsWednesday.isActivated
            }
            buttonTagsThursday.setOnClickListener {
                buttonTagsThursday.isActivated = !buttonTagsThursday.isActivated
            }
            buttonTagsFriday.setOnClickListener {
                buttonTagsFriday.isActivated = !buttonTagsFriday.isActivated
            }
            buttonTagsSaturday.setOnClickListener {
                buttonTagsSaturday.isActivated = !buttonTagsSaturday.isActivated
            }
        }
    }

    private fun setupStartTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartTime
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndTime
        )
    }


    private fun setupSaveButton() {
        binding.buttonSaveRouteForm.setOnClickListener {
            //TODO: upload ke database
            Toast.makeText(requireContext(), "Save session", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }


}