package com.mewlxy.readlib.model

import java.io.Serializable

/**
 * description：
 * author：luoxingyuan
 */
class BookBean : Serializable {
    var id = 0
    var name = ""
    var isLocal = 0
    var favorite = 0
    var cover = ""
    var author = ""
    var desc = ""
    var url = ""
    var lastRead = ""
    var chapters = mutableListOf<ChapterBean>()

    var updateDate = ""
    var isUpdate = 0
    var lastChapter = ""
    var bookFilePath = ""

    var specify = 0
}