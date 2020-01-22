package cn.mewlxy.novel.utils

import android.widget.Toast
import cn.mewlxy.novel.App

/**
 * description：
 * author：luoxingyuan
 */
fun showToast(text: String) {
    Toast.makeText(App.app, text, Toast.LENGTH_SHORT).show()

}