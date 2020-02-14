package com.mewlxy.readlib.interfaces
import com.mewlxy.readlib.model.ChapterBean

interface OnChaptersListener {

    fun onStart()
    fun onSuccess(chapterBeans: MutableList<ChapterBean>)
    fun onError(errorMsg: String)
}
