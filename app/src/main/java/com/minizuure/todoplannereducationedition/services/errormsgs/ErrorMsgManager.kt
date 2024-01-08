package com.minizuure.todoplannereducationedition.services.errormsgs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.minizuure.todoplannereducationedition.R

class ErrorMsgManager {

    /** Set error message for textInputLayout.
     * Use this function if you want to set error message based on type of error that has been defined
     *
     * Usage example:
     *```kotlin
     * ErrorMsgManager().setErrorMessages(
     *     context,
     *     textInputLayout,
     *     textInputEditText,
     *     "20 characters max"
     *     )
     *```
     *
     * @param context : Context
     * @param textInputLayout : TextInputLayout
     * @param textInputEditText : TextInputEditText (optional, based on error requirements)
     * @param errorType : String (type of pre-defined error)
     *
     * */
    fun setErrorMessages(
        context: Context,
        textInputLayout: TextInputLayout,
        textInputEditText: EditText? = null,
        errorType: String
    ) {
        when(errorType) {
            "20 characters max" -> {
                textInputEditText?.doAfterTextChanged { text ->
                    if (text?.length!! > 20) {
                        textInputLayout.error = context.getString(R.string.error_msg_more_than_20)
                    } else {
                        textInputLayout.error = null
                    }
                }
            }
        }
    }
}