package com.mewlxy.readlib.interfaces

import com.mewlxy.readlib.model.ReadRecordBean

interface OnReadRecordListener {

    fun onStart()
    fun onSuccess(readRecord: ReadRecordBean)
    fun onError(errorMsg: String)
}
