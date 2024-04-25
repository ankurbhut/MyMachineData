package com.practical.mydatamachine.shared.callback

import android.view.View

interface IItemViewListener {
    companion object {
        const val CLICK = 1
        const val ADD = 2
        const val DELETE = 3
        const val UPDATE = 4
        const val EMPTY = 5
        const val LOAD_MORE = 6
        const val LONG_CLICK = 7
        const val SELECT=8
    }

    fun onItemClick(view: View?, position: Int?, actionType: Int?, vararg objects: Any?)
    fun onItemLongClick(id: Int?, position: Int?, actionType: Int?, vararg objects: Any?) {  }
}