package com.londonx.kutil.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by London on 2016/11/26.
 * 下一页
 * https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */
class EndlessRecyclerViewScrollListener(
    recyclerView: RecyclerView,
    private val onLoadMore: (page: Int, view: RecyclerView) -> Unit
) :
    RecyclerView.OnScrollListener() {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 6
    // The current offset index of data you have loaded
    private var currentPage = 1
    // The total number of items in the data set after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private val startingPageIndex = 1

    private val mLayoutManager by lazy {
        val temp = recyclerView.layoutManager
        when (temp) {
            is GridLayoutManager -> visibleThreshold *= temp.spanCount
            is StaggeredGridLayoutManager -> visibleThreshold *= temp.spanCount
        }
        temp
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = mLayoutManager!!.itemCount

        val lastVisibleItemPosition = when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions =
                    (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                // get maximum element within the list
                getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            is LinearLayoutManager -> (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            else -> return
        }

        // If the total item cartprototal is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        // If it’s still loading, we check to see if the dataset cartprototal has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item cartprototal.

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too

        // If the total item cartprototal is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset cartprototal has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item cartprototal.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore.invoke(currentPage, view)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = false
    }
}