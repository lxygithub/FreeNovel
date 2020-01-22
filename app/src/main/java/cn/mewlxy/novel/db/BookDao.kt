package cn.mewlxy.novel.db

import androidx.room.*
import cn.mewlxy.novel.model.BookModel

/**
 * description：
 * author：luoxingyuan
 */
@Dao
interface BookDao {
    @Query("SELECT * FROM my_shelf")
    fun getAll(): List<BookModel>?

    @Query("SELECT * FROM my_shelf WHERE name IN (:names)")
    fun loadAllByNames(names: List<String>): List<BookModel>?

    @Query("SELECT * FROM my_shelf WHERE name LIKE '%' || :bookName || '%'")
    fun findByName(bookName: String): List<BookModel>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserts(vararg books: BookModel)

    @Update
    fun update(vararg books: BookModel)

    @Query("SELECT * FROM my_shelf WHERE url = :bookUrl")
    fun queryByUrl(bookUrl: String): BookModel?

    @Delete
    fun delete(book: BookModel)
}
