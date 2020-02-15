package cn.mewlxy.novel.db

import androidx.room.*
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.BookSignModel

/**
 * description：
 * author：luoxingyuan
 */
@Dao
interface BookSignDao {
    @Query("SELECT * FROM my_signs")
    fun getAll(): List<BookSignModel>

    @Query("SELECT * FROM my_signs WHERE `bookUrl`== :bookUrl")
    fun getSignsByBookUrl(bookUrl: String): MutableList<BookSignModel>

    @Query("SELECT * FROM my_signs WHERE `chapterUrl`== :chapterUrl")
    fun getSignsByChapterUrl(chapterUrl: String): BookSignModel?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserts(vararg bookSign: BookSignModel)

    @Update
    fun update(vararg bookSign: BookSignModel)

    @Delete
    fun delete(vararg bookSign: BookSignModel)
}
