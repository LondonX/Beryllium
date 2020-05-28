package com.londonx.kutil.widget

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import splitties.views.inflate

/**
 * Created by London on 2017/7/6.
 * simple adapter for RecyclerView
 */
abstract class SimpleAdapter<T>(@LayoutRes val layoutId: Int, val dataList: ArrayList<T>) :
    RecyclerView.Adapter<SimpleHolder>() {

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        bindData(holder, position, dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder =
        SimpleHolder(parent.inflate(layoutId, false))

    override fun getItemCount(): Int = dataList.size

    abstract fun bindData(holder: SimpleHolder, position: Int, data: T)
}