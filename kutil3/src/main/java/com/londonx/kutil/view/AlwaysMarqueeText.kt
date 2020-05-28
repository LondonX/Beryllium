package com.londonx.kutil.view

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

class AlwaysMarqueeText : TextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        isSingleLine = true
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        if (focused) super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused) super.onWindowFocusChanged(focused)
    }


    override fun isFocused(): Boolean {
        return true
    }
}