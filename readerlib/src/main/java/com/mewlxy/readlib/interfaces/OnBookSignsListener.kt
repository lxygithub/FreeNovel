package com.mewlxy.readlib.interfaces

import com.mewlxy.readlib.model.BookSignTable

interface OnBookSignsListener {

    fun onStart()
    fun onSuccess(chapterBeans: MutableList<out BookSignTable>)
    fun onError(errorMsg: String)
}
