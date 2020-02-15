package cn.mewlxy.novel.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mewlxy.readlib.model.BookSignTable
import java.io.Serializable

/**
 * description：
 * author：luoxingyuan
 */
@Entity(tableName = "my_signs",indices = [Index(value = ["chapterUrl"],
        unique = true)])
data class BookSignModel constructor(@PrimaryKey(autoGenerate = true)
                                var id: Int = 0) : BookSignTable(), Serializable {

}