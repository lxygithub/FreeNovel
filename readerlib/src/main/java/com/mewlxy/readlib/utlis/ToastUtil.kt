package com.mewlxy.readlib.utlis

import android.widget.Toast
import com.mewlxy.readlib.base.ContextProvider

/**
 * description：
 * author：luoxingyuan
 */
fun showToast(text: String) {
    Toast.makeText(ContextProvider.mContext, text, Toast.LENGTH_SHORT).show()

}