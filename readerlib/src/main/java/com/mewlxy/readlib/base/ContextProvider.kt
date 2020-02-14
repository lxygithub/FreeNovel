package com.mewlxy.readlib.base

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.mewlxy.readlib.Constant
import com.mewlxy.readlib.utlis.LocalManageUtil
import com.mewlxy.readlib.utlis.SpUtil


class ContextProvider : ContentProvider() {

    companion object {
        var mContext: Context? = null
    }

    override fun onCreate(): Boolean {
        mContext = context
        SpUtil.init(context)
        LocalManageUtil.setApplicationLanguage(context)
        setNight()
        return false
    }

    private fun setNight() {
        if (SpUtil.getBooleanValue(Constant.NIGHT, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    } //...省略其他必须复写的方法（空实现即可）

}