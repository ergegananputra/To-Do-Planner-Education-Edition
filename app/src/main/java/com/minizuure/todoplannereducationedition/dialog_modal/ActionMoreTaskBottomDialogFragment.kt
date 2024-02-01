package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.minizuure.todoplannereducationedition.databinding.ModalDetailTaskBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog

/**
 * [ActionMoreTaskBottomDialogFragment] is a bottom sheet dialog fragment for DetailFragment
 *
 *
 * Todo:
 * - [ ] Add onCommunitiesAction
 */
class ActionMoreTaskBottomDialogFragment(
    private val taskId : Long,
    private val onEditAction : () -> Unit,
    private val onDeleteAction : () -> Unit,
    private val onResetAction : () -> Unit,
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
            onResetAction()
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