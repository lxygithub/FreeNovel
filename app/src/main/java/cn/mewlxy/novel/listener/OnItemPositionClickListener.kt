package cn.mewlxy.novel.listener

/**
 * description：
 * author：luoxingyuan
 */
interface OnItemPositionClickListener<T> : BaseOnItemClickListener {

    fun itemClick(position: Int, t: T)

}