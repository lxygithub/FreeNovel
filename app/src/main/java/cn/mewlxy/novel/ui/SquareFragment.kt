package cn.mewlxy.novel.ui

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.TypesAdapter
import cn.mewlxy.novel.base.BaseFragment
import cn.mewlxy.novel.constant.Source
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.listener.OnItemViewClickListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.utils.showToast
import kotlinx.android.synthetic.main.fragment_square.*
import kotlinx.android.synthetic.main.search_view.*
import org.jsoup.nodes.Document

/**
 * description：
 * author：luoxingyuan
 *
 */

class SquareFragment private constructor() : BaseFragment(), View.OnClickListener, OnItemViewClickListener<BookModel> {
    private val bookModels = ArrayList<BookModel>()
    private lateinit var typesAdapter: TypesAdapter
    private var domSoup = DomSoup()

    companion object {
        val instance: SquareFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SquareFragment()
        }
    }


    override fun getLayoutResId(): Int {
        return R.layout.fragment_square
    }

    override fun initView() {
        rv_types.layoutManager = LinearLayoutManager(activity)
        tv_search.setOnClickListener(this)
    }

    override fun initData() {
        addObserver(domSoup)
        typesAdapter = TypesAdapter(activity as Context, bookModels)
        rv_types.adapter = typesAdapter
        typesAdapter.onItemViewClickListener = this

        domSoup.getSoup(Source.QUANBEN, object : OnJSoupListener {
            override fun start() {
                showLoading()
            }

            override fun success(document: Document) {
                dismissLoading()
                val typeUrls = document.body().getElementsByClass("nav")[0].getElementsByTag("a")
                val typeRecommendBooks = document.body().getElementsByClass("box")
                val pairs = typeUrls.zip(typeRecommendBooks)
                for (pair in pairs) {
                    val bookModel = BookModel()
                    val it = pair.first
                    bookModel.category = it.text()
                    bookModel.typeUrl = Source.QUANBEN + it.attr("href")
                    val it2 = pair.second
                    bookModel.coverUrl = it2.getElementsByTag("img")[0].attr("src").toString()
                    bookModel.name = it2.getElementsByTag("a")[0].text()
                    bookModel.url = Source.QUANBEN + it2.getElementsByTag("a")[0].attr("href").toString()
                    bookModel.bookAuthor = it2.getElementsByTag("span")[0].text()
                    bookModel.desc = it2.getElementsByTag("p").last().text()
                    bookModel.source = Source.QUANBEN
                    bookModels.add(bookModel)

                }
                typesAdapter.notifyDataSetChanged()
            }

            override fun failed(errMsg: String) {
                dismissLoading()
                showToast(errMsg)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_search -> {
                startActivity(Intent(activity, SearchActivity::class.java))
            }
        }
    }

    //item点击监听
    override fun itemClick(view: View, t: BookModel) {
        when (view.id) {
            R.id.tv_item_type -> {
                startActivity(Intent(activity as Context, BookTypeActivity::class.java)
                        .putExtra("type", t.category)
                        .putExtra("typeUrl", t.typeUrl))
            }
            else -> {
                startActivity(Intent(activity as Context, BookDetailActivity::class.java)
                        .putExtra("name", t.name)
                        .putExtra("bookUrl", t.url))
            }
        }

    }

}