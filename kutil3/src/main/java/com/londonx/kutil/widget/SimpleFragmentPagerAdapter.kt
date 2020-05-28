package com.londonx.kutil.widget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Created by London on 2017/12/26.
 * simple adapter
 */
class SimpleFragmentPagerAdapter(fm: FragmentManager, private vararg val fragments: Fragment) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size
}