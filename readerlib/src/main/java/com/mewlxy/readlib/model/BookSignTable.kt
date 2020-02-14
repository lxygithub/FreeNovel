package com.mewlxy.readlib.model


import java.io.Serializable

/**
 * create by zlj on 2019/11/6
 * describe: 书签数据库
 */
class BookSignTable(val bookId: String, val articleId: String, val content: String) :Serializable {
    val saveTime: Long = System.currentTimeMillis()
    var edit: Boolean = false
}
