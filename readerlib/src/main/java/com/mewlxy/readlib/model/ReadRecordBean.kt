package com.mewlxy.readlib.model


import java.io.Serializable

/**
 * Created by zlj
 */
open class ReadRecordBean : Serializable {
    var bookUrl: String = ""
    //所属的书的id
    var bookMd5: String = ""
    //阅读到了第几章
    var chapterPos: Int = 0
    //当前的页码
    var pagePos: Int = 0
}
