package cn.mewlxy.novel.ui

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.BookListAdapter
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.constant.Source
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.listener.RVOScrollListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.utils.showToast
import kotlinx.android.synthetic.main.activity_search.*
import org.jsoup.nodes.Document

/**
 * description：
 * author：luoxingyuan
 */
class SearchActivity : BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener {
    private val domSoup = DomSoup()
    private var bookModels = ArrayList<BookModel>()
    private lateinit var bookListAdapter: BookListAdapter
    private var currentPage = 1
    private var totalPage = 1
    private var isLoading = false
    private var keyword = ""
    override fun getLayoutResId(): Int {
        return R.layout.activity_search
    }

    override fun initView() {
        rv_search_result.layoutManager = LinearLayoutManager(this)
        et_search.setOnEditorActionListener(this)
        iv_back.setOnClickListener(this)

    }

    override fun initData() {
        addObserver(domSoup)
        bookListAdapter = BookListAdapter(this, bookModels)
        rv_search_result.adapter = bookListAdapter
        rv_search_result.addOnScrollListener(object : RVOScrollListener(rv_search_result.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                search(keyword, currentPage)
            }

            override fun totalPageCount(): Int {
                return totalPage
            }

            override fun isLastPage(): Boolean {
                return currentPage >= totalPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    //搜索
    private fun search(keyword: String, page: Int) {
        domSoup.getSoup(Source.QUANBEN + "/index.php?c=xs&a=search&keywords=${keyword}&page=${page}", object : OnJSoupListener {
            override fun start() {
                isLoading = true
                showLoading()
            }

            override fun success(document: Document) {
                isLoading = false
                dismissLoading()
                val pageCountTxt = document.body().getElementsByClass("list_page")[0].getElementsByTag("span")[1].text()
                currentPage = pageCountTxt.split("/")[0].trim().toInt() + 1
                totalPage = pageCountTxt.split("/")[1].trim().toInt()

                val list = document.body().getElementsByClass("top")
                for (it in list) {
                    val bookModel = BookModel()
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
        })
    }

    //点击搜索
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            keyword = et_search.text.toString().trim()
            if (!TextUtils.isEmpty(keyword)) {
                search(keyword, currentPage)
            } else {
                showToast("关键字不能为空")
            }
        }
        return true
    }

}