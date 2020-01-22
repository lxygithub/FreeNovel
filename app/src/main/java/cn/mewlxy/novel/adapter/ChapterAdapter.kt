package cn.mewlxy.novel.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.mewlxy.novel.R
import cn.mewlxy.novel.listener.OnItemViewClickListener
import cn.mewlxy.novel.model.ChapterModel
import cn.mewlxy.novel.ui.ReadingActivity

/**
 * description：
 * author：luoxingyuan
 */
class ChapterAdapter(var context: Context, var chapters: ArrayList<ChapterModel>) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {
    private lateinit var onItemViewClickListener: OnItemViewClickListener<ChapterModel>

    fun setOnItemViewClickListener(onItemViewClickListener: OnItemViewClickListener<ChapterModel>){
        this.onItemViewClickListener = onItemViewClickListener
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.tvName.text = chapter.name
        holder.itemView.setOnClickListener {
            onItemViewClickListener.itemClick(it,chapter)
        }
    }
}