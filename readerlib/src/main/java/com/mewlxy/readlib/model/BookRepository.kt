package com.mewlxy.readlib.model

import android.content.Context
import com.mewlxy.readlib.Constant
import com.mewlxy.readlib.interfaces.OnBitmapLoadListener
import com.mewlxy.readlib.interfaces.OnBookSignsListener
import com.mewlxy.readlib.interfaces.OnChaptersListener
import com.mewlxy.readlib.interfaces.OnReadRecordListener
import com.mewlxy.readlib.utlis.FileUtils
import com.mewlxy.readlib.utlis.IOUtils.close
import java.io.*

/**
 * Created by newbiechen on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录),BookSignTable书签)
 */
abstract class BookRepository {
    /**
     * 保存阅读记录
     */
    abstract fun saveBookRecord(mBookRecord: ReadRecordBean)

    /**
     * 获取阅读记录
     */
    abstract fun getBookRecord(bookUrl: String, readRecordListener: OnReadRecordListener)

    /**
     * 获取章节列表
     */
    abstract fun chapterBeans(mCollBook: BookBean, onChaptersListener: OnChaptersListener, start: Int = 0)

    /**
     * 获取章节内容
     */
    abstract fun requestChapterContents(mCollBook: BookBean, requestChapters: List<ChapterBean?>, onChaptersListener: OnChaptersListener)

    abstract fun saveBookChaptersWithAsync(bookChapterBeanList: List<ChapterBean>, mCollBook: BookBean)
    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    fun saveChapterInfo(folderName: String, fileName: String, content: String) {
        val filePath = (Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB)
        if (File(filePath).exists()) {
            return
        }
        val str = content.replace("\\\\n\\\\n".toRegex(), "\n")
        val file = BookManager.getBookFile(folderName, fileName)
        //获取流并存储
        var writer: Writer? = null
        try {
            writer = BufferedWriter(FileWriter(file))
            writer.write(str)
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            close(writer)
        }
    }

    /**
     * 加入书架
     */
    abstract fun saveCollBookWithAsync(mCollBook: BookBean)

    /**
     * 书签是否已存在
     */
    abstract fun hasSigned(chapterUrl: String): Boolean

    /**
     * 添加书签
     */
    abstract fun addSign(mBookUrl: String, chapterUrl: String, chapterName: String, bookSignsListener: OnBookSignsListener)

    /**
     * 删除书签
     */
    abstract fun deleteSign(vararg bookSign: BookSignTable)

    /**
     * 获取书签列表
     */
    abstract fun getSigns(bookUrl: String, bookSignsListener: OnBookSignsListener)


    /**
     * 加载插图
     */
    abstract fun loadBitmap(context: Context, imageUrl: String, bitmapLoadListener: OnBitmapLoadListener)

    /**
     * 音量键翻页开关
     */
    abstract fun canTurnPageByVolume(): Boolean
}