package com.mewlxy.readlib.model

import java.io.Serializable

/**
 * description：
 * author：luoxingyuan
 */
class BookBean : Serializable {
    var id = 0
    var name = ""
    var isLocal = false
    var cover = ""
    var author = ""
    var desc = ""
    var url = ""
    var lastRead = ""
    var chapters = mutableListOf<ChapterBean>()

    var updateDate = ""
    var isUpdate = false
    var lastChapter = ""
    var bookFilePath = ""
}