package com.minizuure.todoplannereducationedition.communities.dasbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.databinding.FragmentCommunitiesBinding


class CommunitiesFragment : Fragment() {

    private val binding by lazy {
        FragmentCommunitiesBinding.inflate(layoutInflater)
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

        setupManageMembersButton()
        setupCreateCommunityButton()
        setupCommunityOffButton()
        setupJoinCommunityButton()
    }

    private fun setupCreateCommunityButton() {
        // TODO: Implement create community button
        // by choosing a routine and its members
        Toast.makeText(requireContext(), "Create community button not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun setupCommunityOffButton() {
        // TODO: Implement un publish routine button
        Toast.makeText(requireContext(), "Un publish community button not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun setupJoinCommunityButton() {
        // TODO: Implement join community button
        Toast.makeText(requireContext(), "Join community button not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun setupManageMembersButton() {
        val destination = CommunitiesFragmentDirections.actionCommunitiesFragment2ToCommunityMemberFragment()
        binding.cardViewManageMember.setOnClickListener {
            findNavController().navigate(destination)
        }
    }

}