package com.minizuure.todoplannereducationedition.communities.dasbor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentCommunitiesBinding
import com.minizuure.todoplannereducationedition.dialog_modal.GlobalBottomSheetDialogFragment
import com.minizuure.todoplannereducationedition.dialog_modal.ModalJoinCommunitySheetDialogFragment
import com.minizuure.todoplannereducationedition.dialog_modal.adapter.GlobalAdapter
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog
import com.minizuure.todoplannereducationedition.services.api.worker.DatabaseSyncServices
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CommunitiesFragment : Fragment() {
    private lateinit var routineViewModel : RoutineViewModel

    private val binding by lazy {
        FragmentCommunitiesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        setupViewModelFactory()
        return binding.root
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

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
        binding.buttonCreateCommunity.setOnClickListener {
            lifecycleScope.launch {
                if (MinimumBottomSheetDialog.isDialogOpen) return@launch

                val routineAdapter = GlobalAdapter()


                val bottomSheet = GlobalBottomSheetDialogFragment(
                    title = getString(R.string.select_routine),
                    globalAdapter = routineAdapter,
                    useAdditionalButton = false,

                    )

                routineAdapter.onClickAction = { selected -> onSelectedRoutineForCommunity(selected as RoutineTable, bottomSheet) }

                val routines = withContext(Dispatchers.IO) {
                    routineViewModel.getAll()
                }

                routineAdapter.submitList(routines)
                bottomSheet.show(parentFragmentManager, "CreateCommunityBottomSheet")
            }
        }
    }

    private fun onSelectedRoutineForCommunity(selected: RoutineTable, bottomSheet: GlobalBottomSheetDialogFragment) {
        val textCode = "${getString(R.string.routine_code)} Loading..."
        binding.textViewTitleRoutine.text = selected.title
        binding.textViewCodeRoutine.text = textCode
        binding.textViewDescriptionRoutine.text = selected.description

        UserPreferences(requireActivity()).apply {
            communityId = selected.id.toString()
            isCommunityHost = true
            communityName = selected.title
        }

        Intent(requireActivity().applicationContext, DatabaseSyncServices::class.java).also { intent ->
            intent.action = DatabaseSyncServices.SyncType.UPLOAD.toString()
            requireActivity().startService(intent)
        }

        bottomSheet.closeDialog()
    }

    private fun setupCommunityOffButton() {
        // TODO: Implement un publish routine button
        binding.cardViewRoutine.setOnClickListener {
            Toast.makeText(requireContext(), "Un publish community button not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupJoinCommunityButton() {
        // TODO: Implement join community button
        binding.buttonJoinCommunity.setOnClickListener {
            if (MinimumBottomSheetDialog.isDialogOpen) return@setOnClickListener

            val bottomSheet = ModalJoinCommunitySheetDialogFragment(
                onClickJoin = onClickJoinCommunity()
            )

            bottomSheet.show(parentFragmentManager, "JoinCommunityBottomSheet")
        }

    }

    private fun onClickJoinCommunity(): (String) -> Unit = {
        // TODO: Implement join community button
        Log.v("JoinCommunity", "Join community with code: $it")
        Toast.makeText(requireActivity(), "Join community with code: $it", Toast.LENGTH_SHORT).show()
    }

    private fun setupManageMembersButton() {
        val destination = CommunitiesFragmentDirections.actionCommunitiesFragment2ToCommunityMemberFragment()
        binding.cardViewManageMember.setOnClickListener {
            findNavController().navigate(destination)
        }
    }

}