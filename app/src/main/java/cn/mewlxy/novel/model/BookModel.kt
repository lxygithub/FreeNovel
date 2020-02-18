package cn.mewlxy.novel.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.mewlxy.readlib.model.BookBean
import com.mewlxy.readlib.model.ChapterBean
import java.io.Serializable

/**
 * description：
 * author：luoxingyuan
 */
@Entity(tableName = "my_shelf")
data class BookModel constructor(@PrimaryKey(autoGenerate = true)
                                 var id: Int) : Serializable {

    constructor() : this(0)

    /**
     * 书名
     */
    @ColumnInfo(name = "name")
    var name: String = ""
    /**
     * 地址
     */
    @ColumnInfo(name = "url")
    var url: String = ""
    /**
     * 类型
     */
    @ColumnInfo(name = "category")
    var category: String = ""
    /**
     * 状态
     */
    @ColumnInfo(name = "status")
    var status: String = ""
    /**
     * 类型地址
     */
    @ColumnInfo(name = "typeUrl")
    var typeUrl: String = ""
    /**
     * 封面地址
     */
    @ColumnInfo(name = "coverUrl")
    var coverUrl: String = ""
    /**
     * 作者
     */
    @ColumnInfo(name = "bookAuthor")
    var bookAuthor: String = ""
    /**
     * 描述
     */
    @ColumnInfo(name = "desc")
    var desc: String = ""
    /**
     * 来源网站
     */
    @ColumnInfo(name = "source")
    var source: String = ""
    /**
     * 目录地址
     */
    @ColumnInfo(name = "chaptersUrl")
    var chaptersUrl: String = ""
    /**
     * 字数
     */
    @ColumnInfo(name = "charCount")
    var charCount: Int = 0

    /**
     * 章节数
     */
    @ColumnInfo(name = "chapterCount")
    var chapterCount: Int = 0

    /**
     * 章节列表
     */
    @Ignore
    var chapters = arrayListOf<ChapterModel>()

    /**
     * 是否收藏
     */
    var favorite = 0

    var lastRead = ""

    //阅读到了第几章
    var chapter: Int = 0
    //当前的页码
    var pagePos: Int = 0
    var bookFilePath = ""

    fun convert2BookBean(): BookBean {
        val bookBean = BookBean()
        bookBean.name = this.name
        bookBean.cover = this.coverUrl
        bookBean.desc = this.desc
        bookBean.id = this.id
        bookBean.url = this.url
        bookBean.favorite = this.favorite
        bookBean.bookFilePath = this.bookFilePath
        bookBean.chapters = this.chapters.map {

            return@map it.convert2ChapterBean()
        } as ArrayList<ChapterBean>
        bookBean.author = this.bookAuthor
        return bookBean
    }

    companion object {

        fun convert2BookModel(bookBean: BookBean): BookModel {
            val bookModel = BookModel()
            bookModel.name = bookBean.name
            bookModel.coverUrl = bookBean.cover
            bookModel.desc = bookBean.desc
            bookModel.id = bookBean.id
            bookModel.url = bookBean.url
            bookModel.bookFilePath = bookBean.bookFilePath
            bookModel.favorite = bookBean.favorite
            bookModel.chapters = bookBean.chapters.map {
                return@map ChapterModel.convert2ChapterModel(it)
            } as ArrayList<ChapterModel>
            bookModel.bookAuthor = bookBean.author
            return bookModel
        }
    }
}