package com.minizuure.todoplannereducationedition.surface.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.authentication.AuthenticationActivity
import com.minizuure.todoplannereducationedition.databinding.FragmentProfileBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences


class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }


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
        setupCommunitiesButton()
        setupAuthentication()
        setupSettings()
        setupThemeModeButton()
        marqueeSupport()
    }

    private fun setupCommunitiesButton() {
        val destination = ProfileFragmentDirections.actionProfileFragmentToCommunitiesActivity()
        binding.cardViewCommunities.setOnClickListener {
            findNavController().navigate(destination)
        }

        binding.buttonCommunities.setOnClickListener { binding.cardViewCommunities.performClick() }
    }

    private fun setupThemeModeButton() {
        fun action() {
            val isDarkMode = UserPreferences(requireContext()).isThemeDark

            val newThemeMode = !isDarkMode
            UserPreferences(requireContext()).isThemeDark = newThemeMode

            if (newThemeMode) {
                setDarkMode()
            } else {
                setLightMode()
            }
        }


        binding.buttonThemeMode.setOnClickListener { action() }
        binding.cardViewThemeMode.setOnClickListener { action() }
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.textViewStatusThemeMode.text = getString(R.string.theme_mode_status_light)
        binding.buttonThemeMode.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_light_mode, null)
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding.textViewStatusThemeMode.text = getString(R.string.theme_mode_status_dark)
        binding.buttonThemeMode.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_dark_mode, null)
    }

    private fun setupSettings() {
        fun action() {
            val destination = ProfileFragmentDirections.actionProfileFragmentToSettingsInformationActivity()
            findNavController().navigate(destination)
        }

        binding.buttonSettings.setOnClickListener{action()}
        binding.cardViewSettings.setOnClickListener{action()}

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
        fun action() {
            val intentToRoutinesManagementActivity = Intent(requireActivity(), RoutineManagementActivity::class.java)
            launcherToRoutineManagement.launch(intentToRoutinesManagementActivity)
        }

        binding.buttonRoutinesManagement.setOnClickListener { action() }
        binding.cardViewRoutinesManagement.setOnClickListener { action() }

    }

}