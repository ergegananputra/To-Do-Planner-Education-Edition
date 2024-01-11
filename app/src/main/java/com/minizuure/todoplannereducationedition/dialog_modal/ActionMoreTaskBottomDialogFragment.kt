package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.minizuure.todoplannereducationedition.databinding.ModalDetailTaskBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog
import com.minizuure.todoplannereducationedition.first_layer.detail.DetailFragmentDirections

class ActionMoreTaskBottomDialogFragment(
    private val taskId : Long,
    private val onEditAction : () -> Unit,
    private val onDeleteAction : () -> Unit,
) : MinimumBottomSheetDialog() {
    private val binding by lazy { ModalDetailTaskBottomSheetDialogBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditButton()
        setupResetThisTaskButton()
        setupCommunitiesButton()
        setupDeleteButton()


    }

    private fun setupDeleteButton() {
        binding.cardItemDeleteBottomSheet.setOnClickListener {
            onDeleteAction()
            dismiss()
        }
    }

    private fun setupCommunitiesButton() {
        // TODO : Hide this button if the task is not in communities

        binding.cardItemCommunitiesSettingBottomSheet.setOnClickListener {
            // TODO : Navigate to Communities Setting
            Toast.makeText(requireContext(), "Communities $taskId", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun setupResetThisTaskButton() {
        binding.cardItemResetSectionBottomSheet.setOnClickListener {
            // TODO : Reset this task quiz materials, to-pack, memo, and
            //  delete unique (task with date) from database
            Toast.makeText(requireContext(), "Reset $taskId", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun setupEditButton() {
        binding.cardItemEditBottomSheet.setOnClickListener {
            onEditAction()
            dismiss()
        }
    }



}