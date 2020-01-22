package cn.mewlxy.novel.listener

import android.view.View
import java.time.Year

/**
 * description：
 * author：luoxingyuan
 */
interface OnItemViewClickListener<T> : BaseOnItemClickListener {

    fun itemClick(view: View, t: T)

}