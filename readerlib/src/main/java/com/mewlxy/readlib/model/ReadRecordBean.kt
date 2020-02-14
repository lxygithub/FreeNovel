package com.mewlxy.readlib.model


import java.io.Serializable

/**
 * Created by zlj
 */
class ReadRecordBean :  Serializable {
    //所属的书的id
    var bookId: String = ""
    //阅读到了第几章
    var chapter: Int = 0
    //当前的页码
    var pagePos: Int = 0
}
