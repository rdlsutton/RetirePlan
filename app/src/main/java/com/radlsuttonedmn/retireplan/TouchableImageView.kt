package com.radlsuttonedmn.retireplan

import android.content.Context
import android.util.AttributeSet

class TouchableImageView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}