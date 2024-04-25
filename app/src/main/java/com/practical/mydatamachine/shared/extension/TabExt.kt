package com.practical.mydatamachine.shared.extension

import android.widget.TextView
import com.google.android.material.tabs.TabLayout

fun TabLayout.Tab.setTypeface(style: Int) {
    val tabTextView = view.getChildAt(1) as TextView
    tabTextView.setTypeface(tabTextView.typeface, style)
}
