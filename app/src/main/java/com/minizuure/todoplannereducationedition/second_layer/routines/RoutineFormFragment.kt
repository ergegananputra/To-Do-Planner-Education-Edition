package com.minizuure.todoplannereducationedition.second_layer.routines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentRoutineFormBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity


class RoutineFormFragment : Fragment() {

    val args : RoutineFormFragmentArgs by navArgs()
    private val binding by lazy {
        FragmentRoutineFormBinding.inflate(layoutInflater)
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
    }

    private fun setupSaveButton() {
        binding.buttonSaveRouteForm.setOnClickListener {
            //TODO: upload ke database
            Toast.makeText(requireContext(), "Save routine", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }


}