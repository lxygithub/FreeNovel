package cn.mewlxy.novel.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.ChapterModel

/**
 * description：
 * author：luoxingyuan
 */
@Database(entities = [BookModel::class, ChapterModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
}