package cn.mewlxy.novel.utils

import android.content.res.Resources

/**
 * description：
 * author：luoxingyuan
 */
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()