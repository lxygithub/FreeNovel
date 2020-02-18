package cn.mewlxy.novel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.mewlxy.novel.R
import cn.mewlxy.novel.listener.OnItemPositionClickListener
import cn.mewlxy.novel.model.FileModel

class ExploreAdapter(private val context: Context, private val fileList: MutableList<FileModel>)
    : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {
    var itemPositionClickListener: OnItemPositionClickListener<FileModel>? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tv_item_file_name)
        val ivFileIcon: ImageView = itemView.findViewById(R.id.iv_item_file_icon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_explore, parent, false))
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileModel = fileList[position]
        holder.tvFileName.text = fileModel.name
        holder.ivFileIcon.setImageResource(if (fileModel.isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
        holder.itemView.setOnClickListener {
            itemPositionClickListener?.itemClick(position, fileModel)
        }

    }

}
