package com.londonx.kutil.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.londonx.kutil.R

open class GeneralToolbar(@LayoutRes layout: Int = R.layout.general_toolbar) : Fragment(layout) {
    var backEnabled = true
        set(value) {
            field = value
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(field)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        val toolbar = view as Toolbar
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar ?: return

        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
    }
}