package cn.mewlxy.novel.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.BookSignModel
import cn.mewlxy.novel.model.ChapterModel
import cn.mewlxy.novel.model.ReadRecordModel

/**
 * description：
 * author：luoxingyuan
 */
@Database(entities = [BookModel::class, ChapterModel::class,
    BookSignModel::class, ReadRecordModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun bookSignDao(): BookSignDao
    abstract fun readRecordDao(): ReadRecordDao
}