package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minizuure.todoplannereducationedition.databinding.ModalJoinCommunityBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog

class ModalJoinCommunitySheetDialogFragment(
    private val onClickJoin : (String) -> Unit
) : MinimumBottomSheetDialog() {

    private val binding by lazy {
        ModalJoinCommunityBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJoinCommunityButton()
    }

    private fun setupJoinCommunityButton() {
        // TODO: Implement join community button
        binding.buttonJoinCommunityBottomSheet.setOnClickListener {
            val text = binding.textInputLayoutJoinCommunityBottomSheet.editText!!.text.toString().trim()
            onClickJoin(text)
            closeDialog()
        }
    }

}