package com.minizuure.todoplannereducationedition.settings_information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minizuure.todoplannereducationedition.databinding.FragmentSettingBinding

class SettingsInformationFragment : Fragment() {

    private val binding by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }



}