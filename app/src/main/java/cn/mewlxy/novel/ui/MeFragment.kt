package cn.mewlxy.novel.ui

import cn.mewlxy.novel.R
import cn.mewlxy.novel.base.BaseFragment

/**
 * description：
 * author：luoxingyuan
 *
 */

class MeFragment private constructor() : BaseFragment() {

    companion object {
        val instance: MeFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MeFragment()
        }
    }


    override fun getLayoutResId(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {
    }

    override fun initData() {
    }

}