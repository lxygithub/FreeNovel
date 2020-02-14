package cn.mewlxy.novel.ui

import android.database.sqlite.SQLiteConstraintException
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.ChapterAdapter
import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.constant.Source
import cn.mewlxy.novel.imageloader.ImageLoader
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.listener.OnItemViewClickListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.BookRepositoryImpl
import cn.mewlxy.novel.model.ChapterModel
import cn.mewlxy.novel.utils.showToast
import com.mewlxy.readlib.activity.NovelReadActivity
import com.mewlxy.readlib.model.BookBean
import kotlinx.android.synthetic.main.activity_book.*
import kotlinx.android.synthetic.main.title_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document

/**
 * description：
 * author：luoxingyuan
 */
class BookDetailActivity : BaseActivity(), View.OnClickListener, OnItemViewClickListener<ChapterModel> {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var chapterAdapter: ChapterAdapter
    private val chapters = ArrayList<ChapterModel>()
    private val domSoup: DomSoup = DomSoup()
    private var bookModel: BookModel = BookModel()
    override fun getLayoutResId(): Int {
        return R.layout.activity_book
    }

    override fun initView() {
        rv_chapters.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        val name = intent.getStringExtra("name")
        val bookUrl = intent.getStringExtra("bookUrl")
        tv_title.text = name
        addObserver(domSoup)
        iv_title_back.setOnClickListener(this)
        tv_add_shelf.setOnClickListener(this)
        tv_read.setOnClickListener(this)
        tv_read.setOnClickListener(this)

        chapterAdapter = ChapterAdapter(this, chapters)
        chapterAdapter.setOnItemViewClickListener(this)
        rv_chapters.adapter = chapterAdapter
        domSoup.getSoup(bookUrl + "xiaoshuo.html", object : OnJSoupListener {
            override fun start() {
                showLoading()
            }

            override fun success(document: Document) {
                dismissLoading()
                val element = document.body().getElementsByClass("top").first()
                val coverUrl = element.getElementsByTag("img").attr("src").toString()
                val bookName = element.getElementsByTag("span")[0].text()
                val author = element.getElementsByTag("span")[1].text()
                val category = element.getElementsByTag("span")[2].text()
                val status = element.getElementsByTag("span")[3].text()
                val desc = document.body().getElementsByClass("description").first().getElementsByTag("p").first().text()
                ImageLoader.loadImage(this@BookDetailActivity, iv_cover, coverUrl)
                tv_name.text = bookName
                tv_author.text = "作者：$author"
                tv_type.text = "类别：$category"
                tv_status.text = "状态：$status"

                tv_desc.text = desc
                bookModel.name = bookName
                bookModel.coverUrl = coverUrl
                bookModel.url = bookUrl
                bookModel.bookAuthor = author
                bookModel.category = category
                bookModel.status = status
                bookModel.desc = desc
                bookModel.chaptersUrl = bookUrl + "xiaoshuo.html"
                bookModel.source = Source.QUANBEN

                val chapterTags = document.body().getElementsByTag("li")
                for ((i, it) in chapterTags.withIndex()) {
                    val url = bookUrl + it.getElementsByTag("a").first().attr("href").toString()
                    val chapterName = it.getElementsByTag("a").first().text()
                    val chapterModel = ChapterModel()
                    chapterModel.bookName = bookName
                    chapterModel.bookUrl = bookUrl
                    chapterModel.index = i
                    chapterModel.name = chapterName
                    chapterModel.url = url
                    chapters.add(chapterModel)
                }
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        appDB.chapterDao().inserts(*chapters.toTypedArray())
                    } catch (e: SQLiteConstraintException) {
                    }
                }
                chapterAdapter.notifyDataSetChanged()
            }

            override fun failed(errMsg: String) {
                dismissLoading()
            }
        })

    }

    private fun addToShelf(bookModel: BookModel) {
        showLoading()
        uiScope.launch(Dispatchers.IO) {
            if (!TextUtils.isEmpty(bookModel.url)) {
                val url = appDB.bookDao().queryByUrl(bookModel.url)?.url
                launch(Dispatchers.Main) {
                    if (TextUtils.isEmpty(url)) {
                        launch(Dispatchers.IO) {
                            appDB.bookDao().inserts(bookModel)
                        }
                        showToast("加入书架成功")
                    } else {
                        showToast("该书籍已在书架中")
                    }
                }
            }
            launch(Dispatchers.Main) {
                dismissLoading()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_title_back -> {
                finish()
            }
            R.id.tv_add_shelf -> {
                addToShelf(bookModel)
            }
            R.id.tv_read -> {
                NovelReadActivity.start(this,bookModel.convert2BookBean(),BookRepositoryImpl.instance)
            }
        }
    }

    override fun itemClick(view: View, t: ChapterModel) {
        NovelReadActivity.start(this,bookModel.convert2BookBean(),BookRepositoryImpl.instance)
    }


}