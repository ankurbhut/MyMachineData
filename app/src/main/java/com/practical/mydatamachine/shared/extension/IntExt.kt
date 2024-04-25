package com.practical.mydatamachine.shared.extension

import android.content.Context
import android.util.DisplayMetrics

fun Int.toBoolean(): Boolean = (this == 1)
fun Int.hasFlag(flag: Int) = flag and this == flag
fun Int.withFlag(flag: Int) = this or flag
fun Int.minusFlag(flag: Int) = this and flag.inv()

fun Int.toPx(context: Context) =
    (this * context.resources.displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT

