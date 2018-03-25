package com.seki.saezurishiki.view.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class ZoomPicture @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}