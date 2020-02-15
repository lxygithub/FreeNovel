package com.mewlxy.readlib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mewlxy.readlib.R
import com.mewlxy.readlib.model.BookSignTable


class MarkAdapter(private val mList: List<BookSignTable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null
    var edit: Boolean = false
        set(edit) {
            field = edit
            notifyDataSetChanged()
        }

    val selectList: List<BookSignTable>
        get() {
            return mList.filter {
                return@filter it.edit
            }
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (mContext == null) {
            mContext = viewGroup.context
        }
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.rlv_item_mark, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolder) {
            if (this.edit) {
                viewHolder.mCheck.visibility = View.VISIBLE
                viewHolder.mCheck.setOnCheckedChangeListener { compoundButton, b ->
                    mList[i].edit = b
                }
            } else {
                viewHolder.mCheck.visibility = View.GONE
            }
            viewHolder.mTvMark.text = mList[i].chapterName
            viewHolder.mCheck.isChecked = mList[i].edit
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvMark: TextView = itemView.findViewById(R.id.tvMarkItem)
        var mCheck: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}
