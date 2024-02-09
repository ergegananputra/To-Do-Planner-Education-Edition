package com.minizuure.todoplannereducationedition.communities.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.minizuure.todoplannereducationedition.databinding.FragmentCommunityMemberBinding

class CommunityMemberFragment : Fragment() {
    private val binding by lazy {
        FragmentCommunityMemberBinding.inflate(layoutInflater)
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

        setupSearch()
        setupMemberRecyclerView()
    }

    private fun setupMemberRecyclerView() {
        // Todo: Implement member recycler view
    }

    private fun setupSearch() {
        // Todo: Implement search for community members
        binding.searchBarCommunityMember.editText!!.doAfterTextChanged { text ->
            Toast.makeText(requireContext(), "Search for community members not implemented yet, search : $text", Toast.LENGTH_SHORT).show()
        }
    }

}