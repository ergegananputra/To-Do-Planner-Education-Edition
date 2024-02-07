package com.minizuure.todoplannereducationedition.surface.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.authentication.AuthenticationActivity
import com.minizuure.todoplannereducationedition.databinding.FragmentProfileBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences


class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    private lateinit var auth : FirebaseAuth

    private val launcherToRoutineManagement = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("ProfileFragment", "onCreate: RESULT_OK")
        }
    }

    private val launcherToAuthenticationActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        refreshProfileInfo()
    }

    private fun refreshProfileInfo() {
        val userName = UserPreferences(requireContext()).userName.let {
            it.ifEmpty { null }
        }
        binding.textViewUsername.text = userName ?: getString(R.string.username_default_profile)
        binding.textViewLoginStatus.text =
            if (userName != null) getString(R.string.connected)
            else getString(R.string.login_status_default_profile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        refreshProfileInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRoutinesManagementButton()
        setupAuthentication()
        marqueeSupport()
    }

    private fun marqueeSupport() {
        binding.textViewUsername.isSelected = true

    }

    private fun setupAuthentication() {
        binding.cardViewLoginInfo.setOnClickListener {
            val intentToAuthenticationActivity = Intent(requireActivity(), AuthenticationActivity::class.java)
            launcherToAuthenticationActivity.launch(intentToAuthenticationActivity)
        }
    }

    private fun setupRoutinesManagementButton() {
        binding.cardViewRoutinesManagement.setOnClickListener {
            val intentToRoutinesManagementActivity = Intent(requireActivity(), RoutineManagementActivity::class.java)
            launcherToRoutineManagement.launch(intentToRoutinesManagementActivity)
        }
    }

}