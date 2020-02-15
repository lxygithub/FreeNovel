package com.mewlxy.readlib.model


import java.io.Serializable

/**
 * create by zlj on 2019/11/6
 * describe: 书签数据库
 */
open class BookSignTable : Serializable {
    var bookUrl = ""
    var chapterUrl = ""
    var chapterName = ""
    var saveTime = System.currentTimeMillis()
    var edit: Boolean = false
}
