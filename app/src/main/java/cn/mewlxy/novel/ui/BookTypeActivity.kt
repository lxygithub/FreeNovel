package cn.mewlxy.novel.ui

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.BookListAdapter
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.constant.Source
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.listener.RVOScrollListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.utils.showToast
import org.jsoup.nodes.Document

/**
 * description：
 * author：luoxingyuan
 */
class BookTypeActivity : BaseActivity(), View.OnClickListener, OnJSoupListener {
    private var currentPage: Int = 1
    private var totalPageCount: Int = 1
    private lateinit var type: String
    private lateinit var typeUrl: String
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvSearch: TextView
    private lateinit var rvBooks: RecyclerView
    private lateinit var bookListAdapter: BookListAdapter
    private var bookModels = ArrayList<BookModel>()
    private var domSoup: DomSoup = DomSoup()
    private var isLoading = false
    override fun getLayoutResId(): Int {
        return R.layout.activity_book_type
    }

    override fun initView() {
        ivBack = findViewById(R.id.iv_title_back)
        tvTitle = findViewById(R.id.tv_title)
        tvSearch = findViewById(R.id.tv_search)
        rvBooks = findViewById(R.id.rv_books)
        rvBooks.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        addObserver(domSoup)
        type = intent.getStringExtra("type")
        typeUrl = intent.getStringExtra("typeUrl")
        tvTitle.text = type
        ivBack.setOnClickListener(this)
        tvSearch.setOnClickListener(this)
        getBookList(currentPage)
        bookListAdapter = BookListAdapter(this, bookModels)
        rvBooks.adapter = bookListAdapter
        rvBooks.addOnScrollListener(object : RVOScrollListener(rvBooks.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                getBookList(currentPage)
            }

            override fun totalPageCount(): Int {
                return totalPageCount
            }

            override fun isLastPage(): Boolean {
                return currentPage >= totalPageCount
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_title_back -> {
                finish()
            }
            R.id.tv_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
        }
    }

    private fun getBookList(page: Int) {
        domSoup.getSoup(typeUrl.replace(".html", "-$page.html"), this)
    }


    //-------------------获取分类列表----------------------------
    override fun start() {
        isLoading = true
        showLoading()
    }

    override fun success(document: Document) {
        isLoading = false
        dismissLoading()
        val pageText = document.body().getElementsByClass("list_page").first().getElementsByTag("span")[1].text()
        currentPage = pageText.split("/")[0].trim().toInt()+1
        totalPageCount = pageText.split("/")[1].trim().toInt()
        val list = document.body().getElementsByClass("top")
        for (it in list) {
            val bookModel = BookModel()
            bookModel.category = type
            bookModel.typeUrl = typeUrl
            bookModel.coverUrl = it.getElementsByTag("img")[0].attr("src").toString()
            bookModel.name = it.getElementsByTag("a")[0].text()
            bookModel.url = Source.QUANBEN + it.getElementsByTag("a")[0].attr("href").toString()
            bookModel.bookAuthor = it.getElementsByTag("span")[0].text()
            bookModel.desc = it.getElementsByTag("p").last().text()
            bookModel.source = Source.QUANBEN
            bookModels.add(bookModel)
        }
        bookListAdapter.notifyDataSetChanged()
    }

    override fun failed(errMsg: String) {
        isLoading = false
        dismissLoading()
        showToast(errMsg)
    }

}