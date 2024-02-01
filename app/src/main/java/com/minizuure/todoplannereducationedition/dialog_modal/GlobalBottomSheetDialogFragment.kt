package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ModalBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.adapter.GlobalAdapter
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog

class GlobalBottomSheetDialogFragment(
    private val globalAdapter : GlobalAdapter,
    private val title : String? = null,
    private val useAdditionalButton : Boolean = false,
    private val additionalButtonText : String? = null,
    private val additionalButtonLogic : () -> Unit = {}
) : MinimumBottomSheetDialog() {

    private val binding by lazy { ModalBottomSheetDialogBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTitle()
        setupRecyclerView()
        setupAdditionalButton()


    }

    private fun setupRecyclerView() {
        binding.recyclerViewItemBottomSheet.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = globalAdapter
        }
    }

    private fun setupAdditionalButton() {
        if (useAdditionalButton) {
            binding.includeCustomAddButtonBottomSheet.root.visibility = View.VISIBLE
            binding.includeCustomAddButtonBottomSheet.buttonItemBottomSheet.text = "+"
            binding.includeCustomAddButtonBottomSheet.textViewItemBottomSheet.text = additionalButtonText
            binding.includeCustomAddButtonBottomSheet.cardViewItemGeneral.setOnClickListener {
                additionalButtonLogic()
                dismiss()
            }
        } else {
            binding.includeCustomAddButtonBottomSheet.root.visibility = View.GONE
        }
    }

    private fun setupTitle() {
        val bottomSheetTitle = title ?: getString(R.string.select)
        binding.textViewTitleBottomSheet.text = bottomSheetTitle
    }


}