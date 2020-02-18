package cn.mewlxy.novel.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.mewlxy.novel.R
import cn.mewlxy.novel.imageloader.ImageLoader
import cn.mewlxy.novel.listener.OnItemPositionClickListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.ui.BookDetailActivity

/**
 * description：
 * author：luoxingyuan
 */
class ShelfAdapter(val context: Context, val books: ArrayList<BookModel>) : RecyclerView.Adapter<ShelfAdapter.ViewHolder>() {
    private var manage = false
    var onItemDeleteListener: OnItemDeleteListener<BookModel>? = null
    var itemPositionClickListener: OnItemPositionClickListener<BookModel>? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.iv_item_cover)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val ivDelete: ImageView = itemView.findViewById(R.id.iv_item_del)

    }

    fun setManageMode(manage: Boolean) {
        this.manage = manage
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shelf, parent, false))
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookModel = books[position]
        holder.ivDelete.visibility = if (manage) View.VISIBLE else View.GONE
        holder.ivCover.alpha = if (bookModel.url.isNotEmpty() && manage) 0.5f else 1f
        ImageLoader.loadImage(context as Activity, holder.ivCover, bookModel.coverUrl)
        holder.tvName.text = bookModel.name
        if (manage) {
            holder.itemView.setOnClickListener {
                onItemDeleteListener?.deleteItem(bookModel)
            }
        } else {
            holder.itemView.setOnClickListener {
                itemPositionClickListener?.itemClick(position, bookModel)
            }
        }
    }

    interface OnItemDeleteListener<T> {
        fun deleteItem(t: T)
    }
}