package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.minizuure.todoplannereducationedition.databinding.ModalDetailTaskBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog

class ActionMoreTaskBottomDialogFragment(
    private val taskId : Long,
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
            // TODO : Hapus seluruh task yang mirip dengan task ini,
            //  include unique task (task with date).
            //  Pastikan ada alert dialog untuk konfirmasi
            Toast.makeText(requireContext(), "Delete $taskId", Toast.LENGTH_SHORT).show()
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
            // TODO : Navigate to Task Form
            Toast.makeText(requireContext(), "Edit $taskId", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }



}