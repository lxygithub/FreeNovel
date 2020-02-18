package com.mewlxy.readlib.interfaces

import android.graphics.Bitmap

interface OnBitmapLoadListener {

    fun onLoadStart()
    fun onResourceReady(resource: Bitmap)
    fun onError(errorMsg: String)
}
