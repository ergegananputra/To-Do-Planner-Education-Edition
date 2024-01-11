package com.minizuure.todoplannereducationedition.dialog_modal.preset

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class MinimumBottomSheetDialog : BottomSheetDialogFragment() {
    override fun onStart() {
        super.onStart()
        isDialogOpen = true
    }

    override fun onStop() {
        super.onStop()
        isDialogOpen = false
    }

    fun closeDialog() {
        dismiss()
    }

    companion object {
        var isDialogOpen = false
    }
}