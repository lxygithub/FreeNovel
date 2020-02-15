package cn.mewlxy.novel.db

import androidx.room.*
import cn.mewlxy.novel.model.ReadRecordModel

/**
 * description：
 * author：luoxingyuan
 */
@Dao
interface ReadRecordDao {
    @Query("SELECT * FROM my_read_records")
    fun getAll(): List<ReadRecordModel>

    @Query("SELECT * FROM my_read_records WHERE `bookMd5`== :bookMd5")
    fun getReadRecord(bookMd5: String): ReadRecordModel


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(vararg bookSign: ReadRecordModel)

    @Update
    fun update(vararg bookSign: ReadRecordModel)

    @Delete
    fun delete(vararg bookSign: ReadRecordModel)
}
