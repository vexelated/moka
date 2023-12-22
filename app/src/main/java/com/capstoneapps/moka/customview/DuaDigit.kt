package com.capstoneapps.moka.customview

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class DuaDigit : TextInputEditText {

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    private fun initialize() {
        inputType = InputType.TYPE_CLASS_NUMBER
        filters = arrayOf(InputFilter.LengthFilter(2), DigitsInputFilter())
    }

    private class DigitsInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            // Only allow digits 0-9
            for (i in start until end) {
                if (!Character.isDigit(source?.get(i) ?: '0')) {
                    return ""
                }
            }
            return null
        }
    }

    // Add this method to control editability
    fun setCustomEditable(editable: Boolean) {
        isFocusable = editable
        isFocusableInTouchMode = editable
        isClickable = editable
        isCursorVisible = editable
        // If needed, you can also disable long click
        isLongClickable = editable
    }
}
