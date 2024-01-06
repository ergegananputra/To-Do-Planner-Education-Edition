package com.minizuure.todoplannereducationedition.services.errormsgs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
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
        textInputEditText: TextInputEditText? = null,
        errorType: String
    ) {
        when(errorType) {
            "20 characters max" -> {
                // untested
                textInputEditText?.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if(p0.toString().length > 20) {
                            textInputLayout.isErrorEnabled = true
                            textInputLayout.error = context.resources.getString(R.string.error_msg_more_than_20)
                            textInputEditText.keyListener = null
                        } else {
                            textInputLayout.isErrorEnabled = false
                            textInputLayout.error = null
                            textInputEditText.keyListener = DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {}
                })
            }
        }
    }
}