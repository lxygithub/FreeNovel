package cn.mewlxy.novel

import android.app.Application
import androidx.room.Room
import cn.mewlxy.novel.db.AppDatabase

lateinit var appDB: AppDatabase

class App : Application() {

    companion object {

        lateinit var app: App
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        appDB = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "db_novel"
        ).build()
    }


    /**
     * glide 全局配置
     */
    fun glideConfig() {

    }

}
