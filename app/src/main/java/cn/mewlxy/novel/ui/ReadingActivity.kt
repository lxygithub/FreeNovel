package cn.mewlxy.novel.ui

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.ChapterAdapter
import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.listener.OnItemViewClickListener
import cn.mewlxy.novel.model.ChapterModel
import cn.mewlxy.novel.utils.showToast
import kotlinx.android.synthetic.main.activity_reading.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.view_index.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document


/**
 * description：
 * author：luoxingyuan
 */
class ReadingActivity : BaseActivity(), View.OnClickListener, OnItemViewClickListener<ChapterModel> {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val domSoup = DomSoup()
    private var contentStringBuilder = StringBuilder()
    private val chapters = ArrayList<ChapterModel>()
    private lateinit var chapter: ChapterModel
    private lateinit var chapterAdapter: ChapterAdapter
    override fun getLayoutResId(): Int {
        return R.layout.activity_reading
    }

    override fun initView() {
        rv_index.layoutManager = LinearLayoutManager(this)

    }

    override fun initData() {
        chapter = intent.getParcelableExtra("chapter")
        chapterAdapter = ChapterAdapter(this, chapters)
        chapterAdapter.setOnItemViewClickListener(this)
        rv_index.adapter = chapterAdapter
        addObserver(domSoup)

        iv_back.setOnClickListener(this)
        page_view.setOnClickListener(this)
        read_tv_category.setOnClickListener(this)
        tv_light.setOnClickListener(this)
        tv_cache.setOnClickListener(this)
        tv_setting.setOnClickListener(this)

        val layoutTransition = LayoutTransition()
        (container as ViewGroup).layoutTransition = layoutTransition

        getCachedChapterContent(chapter.url)
        getCacheChapters(chapter.bookUrl)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.page_view -> {
                if (page_view_title.visibility == View.GONE && layout_index.visibility == View.GONE) {
                    page_view_title.visibility = View.VISIBLE
                } else {
                    page_view_title.visibility = View.GONE
                }
                if (bottom_menu.visibility == View.GONE && layout_index.visibility == View.GONE) {
                    bottom_menu.visibility = View.VISIBLE
                } else {
                    bottom_menu.visibility = View.GONE
                }
                if (layout_index.visibility == View.VISIBLE) {
                    layout_index.visibility = View.GONE
                }
                if (layout_setting.visibility == View.VISIBLE) {
                    layout_setting.visibility = View.GONE
                }
                layout_light.visibility = View.GONE
            }
            R.id.read_tv_category -> {
                if (layout_index.visibility == View.GONE) {
                    page_view_title.visibility = View.GONE
                    layout_light.visibility = View.GONE
                    bottom_menu.visibility = View.GONE
                    layout_index.visibility = View.VISIBLE
                }
            }
            R.id.tv_light -> {
                layout_light.visibility = View.VISIBLE
            }
            R.id.tv_setting -> {
                layout_setting.visibility = View.GONE
                if (layout_setting.visibility == View.GONE) {
                    layout_setting.visibility = View.VISIBLE
                } else {
                    layout_setting.visibility = View.GONE
                }
            }
        }
    }

    //---------------------------------获取章节内容-------------------------------


    private fun getChapterContent(url: String) {
        domSoup.getSoup(url, object : OnJSoupListener {
            override fun start() {
                showLoading()
            }

            override fun success(document: Document) {
                dismissLoading()

                val paragraphTags = document.body().getElementById("content").getElementsByTag("p")
                contentStringBuilder.clear()
                for (p in paragraphTags) {
                    contentStringBuilder.append("\t\t\t\t").append(p.text()).append("\n\n")
                }
                chapter.content = contentStringBuilder.toString()
//                page_view.text = chapter.content
                cacheChapter(chapter)
            }

            override fun failed(errMsg: String) {
                dismissLoading()
                showToast(errMsg)
            }
        })
    }

    /**
     * 获取缓存的章节
     */
    private fun getCachedChapterContent(url: String) {
        showLoading()
        uiScope.launch(Dispatchers.IO) {
            val chapterContent = appDB.chapterDao().getChapterContent(url)
            if (chapterContent.isNullOrEmpty()) {
                launch(Dispatchers.Main) {
                    getChapterContent(url)
                }
            } else {
                page_view.text = chapterContent
            }
        }
        dismissLoading()
    }

    /**
     * 缓存章节
     */
    fun cacheChapter(chapter: ChapterModel) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.chapterDao().updates(chapter)
        }
    }

    /**
     * 获取章节列表
     */
    private fun getCacheChapters(bookUrl: String) {
        uiScope.launch(Dispatchers.IO) {
            chapters.clear()
            chapters.addAll(appDB.chapterDao().getChaptersByBookUrl(bookUrl)!!)
        }
        chapterAdapter.notifyDataSetChanged()
    }

    override fun itemClick(view: View, t: ChapterModel) {
        getCachedChapterContent(t.url)
        layout_index.visibility = View.GONE
    }


}