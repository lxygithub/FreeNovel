package cn.mewlxy.novel

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import cn.mewlxy.novel.db.AppDatabase
import com.mewlxy.readlib.Constant
import java.io.File
import kotlin.properties.Delegates

lateinit var appDB: AppDatabase

class App : Application() {

    companion object {

        lateinit var app: App
        var context: Context by Delegates.notNull()
            private set

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
