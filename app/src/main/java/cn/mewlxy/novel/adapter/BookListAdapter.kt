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
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.ui.BookDetailActivity

/**
 * description：
 * author：luoxingyuan
 */
class BookListAdapter(var context: Context, var bookModels: ArrayList<BookModel>) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCover: ImageView = itemView.findViewById(R.id.iv_item_cover)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvAuthor: TextView = itemView.findViewById(R.id.tv_item_author)
        var tvDesc: TextView = itemView.findViewById(R.id.tv_item_desc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book, parent, false))
    }

    override fun getItemCount(): Int {
        return bookModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookModel = bookModels[position]
        holder.tvName.text = bookModel.name
        holder.tvAuthor.text = "作者：" + bookModel.bookAuthor
        holder.tvDesc.text = bookModel.desc
        ImageLoader.loadImage(context as Activity, holder.ivCover, bookModel.coverUrl)
        holder.itemView.setOnClickListener(View.OnClickListener {
            context.startActivity(Intent(context, BookDetailActivity::class.java)
                    .putExtra("name",bookModel.name)
                    .putExtra("bookUrl",bookModel.url))
        })
    }

}