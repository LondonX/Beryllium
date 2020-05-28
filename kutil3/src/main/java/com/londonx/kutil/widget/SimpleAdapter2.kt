package com.londonx.kutil.widget

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Created by London on 2017/11/03.
 * super simple adapter for RecyclerView
 */
abstract class SimpleAdapter2<T>(@LayoutRes val layoutId2: Int) :
        SimpleAdapter<T>(layoutId2, arrayListOf()) {
    override fun bindData(holder: SimpleHolder, position: Int, data: T) {
        bindData2(holder.itemView.context, holder.itemView, position, data)
    }

    abstract fun bindData2(context: Context, itemView: View, position: Int, data: T)
}