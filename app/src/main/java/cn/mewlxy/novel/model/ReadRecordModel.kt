package cn.mewlxy.novel.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mewlxy.readlib.model.ReadRecordBean
import java.io.Serializable

/**
 * description：
 * author：luoxingyuan
 */
@Entity(tableName = "my_read_records", indices = [Index(value = ["bookMd5"],
        unique = true)])
open class ReadRecordModel constructor(@PrimaryKey(autoGenerate = true)
                                       var id: Int = 0) : ReadRecordBean(), Serializable {


    fun convert2ReadRecordBean(): ReadRecordBean {
        val recordBean = ReadRecordBean()
        recordBean.bookMd5 = this.bookMd5
        recordBean.bookUrl = this.bookUrl
        recordBean.chapterPos = this.chapterPos
        recordBean.pagePos = this.pagePos
        return recordBean
    }

    companion object {

        fun createReadRecordModel(recordBean: ReadRecordBean): ReadRecordModel {
            val readRecordModel = ReadRecordModel()
            readRecordModel.bookMd5 = recordBean.bookMd5
            readRecordModel.bookUrl = recordBean.bookUrl
            readRecordModel.chapterPos = recordBean.chapterPos
            readRecordModel.pagePos = recordBean.pagePos
            return readRecordModel
        }
    }
}