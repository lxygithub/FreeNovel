package cn.mewlxy.novel.db

import androidx.room.*
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.ChapterModel

/**
 * description：
 * author：luoxingyuan
 */
@Dao
interface ChapterDao {
    @Query("SELECT * FROM cached_chapters WHERE bookUrl=:bookUrl AND `index`>=:start ORDER  BY `index` ASC LIMIT :limit")
    fun getChaptersByBookUrl(bookUrl: String, start: Int = 0, limit: Int = 100): MutableList<ChapterModel>

    @Query("SELECT * FROM cached_chapters WHERE url=:url")
    fun getChapter(url: String): ChapterModel?

    @Query("SELECT content FROM cached_chapters WHERE url=:url")
    fun getChapterContent(url: String): String?


    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserts(vararg chapters: ChapterModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updates(vararg chapters: ChapterModel)


    @Query("DELETE FROM cached_chapters WHERE bookUrl=:bookUrl")
    fun deleteBookByBookUrl(bookUrl: String)
}
