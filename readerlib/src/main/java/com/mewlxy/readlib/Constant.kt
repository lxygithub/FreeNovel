package com.mewlxy.readlib

import android.graphics.Color
import com.mewlxy.readlib.utlis.FileUtils


import java.io.File

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛
/**
 * Created by zlj on 2019/7/27.
 * desc: 常量
 */
object Constant {

    const val NIGHT = "NIGHT"
    const val Language = "Language"
    const val BookSort = "BookSort"
    const val Uid = "Uid"
    const val Sex = "Sex"
    const val Type = "Type"
    const val DateType = "DateType"
    const val BookGuide = "BookGuide"  //图书引导是否提示过

    const val FORMAT_BOOK_DATE = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_TIME = "HH:mm"
    const val COMMENT_SIZE = 10

    const val FeedBackEmail = "390057892@qq.com"

    /**
     * 百度语音合成
     */
    const val appId = "16826023"
    const val appKey = "vEuU5gIWGwq5hivdTAaKz0P9"
    const val secretKey = "FcWRYUIrOPyE7dy51qfYZmg8Y1ZyP1c4 "

    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    @kotlin.jvm.JvmField
    var BOOK_CACHE_PATH: String = (FileUtils.getDownloadPath() + File.separator
            + "free_novel" + File.separator)

    @kotlin.jvm.JvmField
    val tagColors = intArrayOf(
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E"),
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5")
    )


    interface ResultCode {
        companion object {
            const val RESULT_IS_COLLECTED = "result_is_collected"
        }
    }
}
