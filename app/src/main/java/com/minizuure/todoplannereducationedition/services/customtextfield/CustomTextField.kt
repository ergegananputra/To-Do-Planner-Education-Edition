package com.minizuure.todoplannereducationedition.services.customtextfield

import android.content.Context
import com.google.android.material.textfield.TextInputLayout

class CustomTextField(context: Context) {

    fun setDropdownTextField(
        textInputLayout: TextInputLayout,
        onClick : () -> Unit
    ) {
        textInputLayout.editText?.apply {
            isFocusable = false
            isClickable = true
            isCursorVisible = false
        }

        textInputLayout.editText?.setOnClickListener {
            it.rootView.clearFocus()
            onClick()
        }

        textInputLayout.setEndIconOnClickListener {
            it.rootView.clearFocus()
            onClick()
        }
    }
}