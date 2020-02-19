package cn.mewlxy.novel.ui

import android.content.Intent
import cn.mewlxy.novel.MainActivity
import cn.mewlxy.novel.R
import cn.mewlxy.novel.base.BaseActivity

/**
 * description：
 * author：luoxingyuan
 */
class SplashActivity : BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
    }

    override fun initData() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}