package cn.mewlxy.novel.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mewlxy.readlib.model.ChapterBean

/**
 * 章节
 * description：
 * author：luoxingyuan
 */
@Entity(tableName = "cached_chapters", indices = [Index(value = ["url"],
        unique = true)])
data class ChapterModel constructor(@PrimaryKey(autoGenerate = true)
                                    var id: Int) : Parcelable {
    constructor() : this(0)

    /**
     * 书名
     */
    @ColumnInfo(name = "bookName")
    var bookName: String = ""

    /**
     * 书籍地址
     */
    @ColumnInfo(name = "bookUrl")
    var bookUrl: String = ""

    /**
     * 章节号
     */
    @ColumnInfo(name = "index")
    var index: Int = 0

    /**
     * 章节名称
     */
    @ColumnInfo(name = "name")
    var name: String = ""

    /**
     * 内容
     */
    @ColumnInfo(name = "content")
    var content: String = ""

    /**
     * 章节地址
     */
    @ColumnInfo(name = "url")
    var url: String = ""

    constructor(parcel: Parcel) : this(0) {
        bookName = parcel.readString()!!
        bookUrl = parcel.readString()!!
        index = parcel.readInt()
        name = parcel.readString()!!
        content = parcel.readString()!!
        url = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookName)
        parcel.writeString(bookUrl)
        parcel.writeInt(index)
        parcel.writeString(name)
        parcel.writeString(content)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterModel> {
        override fun createFromParcel(parcel: Parcel): ChapterModel {
            return ChapterModel(parcel)
        }

        override fun newArray(size: Int): Array<ChapterModel?> {
            return arrayOfNulls(size)
        }
    }

    fun convert2ChapterBean():ChapterBean{
        val chapterBean = ChapterBean()
        chapterBean.id = this.id
        chapterBean.name = this.name
        chapterBean.url = this.url
        chapterBean.content = this.content
        chapterBean.bookName = this.bookName
        chapterBean.bookUrl = this.bookUrl
        return chapterBean

    }

}