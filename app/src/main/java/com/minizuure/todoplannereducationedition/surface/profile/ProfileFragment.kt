package com.minizuure.todoplannereducationedition.surface.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentProfileBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity


class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    private val launcherToRoutineManagement = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("ProfileFragment", "onCreate: RESULT_OK")
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

        setupRoutinesManagementButton()
    }

    private fun setupRoutinesManagementButton() {
        binding.cardViewRoutinesManagement.setOnClickListener {
            val intentToRoutinesManagementActivity = Intent(requireActivity(), RoutineManagementActivity::class.java)
            launcherToRoutineManagement.launch(intentToRoutinesManagementActivity)
        }
    }

}