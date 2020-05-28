package com.londonx.kutil.widget

import android.graphics.Color
import android.widget.TextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by London on 2017/10/24.
 * 验证码倒计时
 */
class VerificationCounter(
    private val btnFetch: TextView,
    private val colorDisabled: Int = Color.LTGRAY,
    private val intervalSec: Int = 30,
    private val fmt: String = "%s(%d)"
) {
    private val defaultColor = btnFetch.currentTextColor
    private val defaultText = btnFetch.text

    private var counting: Job? = null
    private var sec = intervalSec

    fun start() {
        counting?.cancel()
        btnFetch.isEnabled = false
        btnFetch.setTextColor(colorDisabled)
        sec = intervalSec
        counting = MainScope().launch {
            while (sec > 0) {
                if (!btnFetch.isAttachedToWindow) {
                    return@launch
                }
                btnFetch.text = String.format(fmt, defaultText, sec)
                sec--
                delay(1000)
            }
            btnFetch.isEnabled = true
            btnFetch.setTextColor(defaultColor)
            btnFetch.text = defaultText
        }
    }

    fun stop() {
        sec = 0
    }
}