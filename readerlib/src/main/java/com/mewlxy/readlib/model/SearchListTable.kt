package com.mewlxy.readlib.model



import java.io.Serializable

/**
 * create by zlj on 2019/6/19
 * describe:
 */
class SearchListTable: Serializable {

    var key: String = ""
    var saveTime: Long = 0


    override fun equals(other: Any?): Boolean {
        return if (other != null && other.toString() == key) {
            true
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + saveTime.hashCode()
        return result
    }
}
