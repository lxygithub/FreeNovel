package cn.mewlxy.novel.model

import android.text.TextUtils
import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.utils.showToast
import com.mewlxy.readlib.interfaces.OnChaptersListener
import com.mewlxy.readlib.model.BookBean
import com.mewlxy.readlib.model.BookRepository
import com.mewlxy.readlib.model.ChapterBean
import com.mewlxy.readlib.model.ReadRecordBean
import com.mewlxy.readlib.utlis.MD5Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * description：
 * author：luoxingyuan
 */
class BookRepositoryImpl : BookRepository() {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val domSoup = DomSoup()
    var lastSub: Subscription? = null

    companion object {
        val instance: BookRepositoryImpl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BookRepositoryImpl()
        }
    }


    private fun getChapterContent(chapterBean: ChapterBean): Single<ChapterBean> {

        return Single.create {
            uiScope.launch(Dispatchers.IO) {
                val chapterContent = appDB.chapterDao().getChapterContent(chapterBean.url)
                uiScope.launch(Dispatchers.Main) {
                    if (chapterContent.isNullOrBlank()) {
                        domSoup.getSoup(chapterBean.url, object : OnJSoupListener {
                            override fun start() {
                            }

                            override fun success(document: Document) {
                                val paragraphTags = document.body().getElementById("content")
                                        .getElementsByTag("p")
                                val stringBuilder = StringBuilder()
                                for (p in paragraphTags) {
                                    stringBuilder.append("\t\t\t\t").append(p.text()).append("\n\n")
                                }
                                chapterBean.content = stringBuilder.toString()
                                it.onSuccess(chapterBean)

                                uiScope.launch(Dispatchers.IO) {
                                    val chapterModel = ChapterModel()
                                    chapterModel.id = chapterBean.id
                                    chapterModel.name = chapterBean.name
                                    chapterModel.url = chapterBean.url
                                    chapterModel.content = chapterBean.content
                                    chapterModel.bookName = chapterBean.bookName
                                    chapterModel.bookUrl = chapterBean.bookUrl
                                    appDB.chapterDao().updates(chapterModel)
                                }
                            }

                            override fun failed(errMsg: String) {
                                it.onError(Throwable(errMsg))
                            }
                        })
                    } else {
                        chapterBean.content = chapterContent
                        it.onSuccess(chapterBean)
                    }
                }
            }

        }
    }

    override fun saveBookRecord(mBookRecord: ReadRecordBean?) {
    }

    override fun getBookRecord(bookId: String?): ReadRecordBean? {
        return ReadRecordBean()
    }

    override fun chapterBeans(mCollBook: BookBean, onChaptersListener: OnChaptersListener, start: Int) {
        onChaptersListener.onStart()
        try {
            uiScope.launch(Dispatchers.IO) {
                val chapters = arrayListOf<ChapterBean>()
                chapters.addAll(appDB.chapterDao().getChaptersByBookUrl(mCollBook.url, start = start).map {
                    return@map it.convert2ChapterBean()
                })
                uiScope.launch(Dispatchers.Main) {
                    onChaptersListener.onSuccess(chapters)
                }
            }
        } catch (e: Exception) {
            onChaptersListener.onError(e.toString())
        }
    }

    override fun requestChapterContents(mCollBook: BookBean, requestChapters: List<ChapterBean?>, onChaptersListener: OnChaptersListener) {
        lastSub?.cancel()
        onChaptersListener.onStart()
        val singleList = requestChapters.map {
            return@map getChapterContent(it!!)
        }

        val newChapters = arrayListOf<ChapterBean>()
        Single.concat(singleList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ChapterBean> {
                    override fun onComplete() {
                        onChaptersListener.onSuccess(newChapters)
                    }

                    override fun onSubscribe(s: Subscription?) {
                        s?.request(Int.MAX_VALUE.toLong())
                        lastSub = s

                    }

                    override fun onNext(chapterBean: ChapterBean) {
                        newChapters.add(chapterBean)
                        //存储章节内容到本地文件
                        if (chapterBean.content.isNotBlank()) {
                            saveChapterInfo(MD5Utils.strToMd5By16(chapterBean.bookUrl)!!, chapterBean.name, chapterBean.content)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        onChaptersListener.onError(t.toString())
                    }

                })
    }

    override fun saveCollBook(mCollBook: BookBean?) {
    }

    override fun saveBookChaptersWithAsync(bookChapterBeanList: List<ChapterBean?>?, mCollBook: BookBean?) {
    }

    override fun saveCollBookWithAsync(mCollBook: BookBean) {

        val bookModel = BookModel.convert2BookModel(mCollBook)
        if (!TextUtils.isEmpty(bookModel.url)) {
            uiScope.launch(Dispatchers.IO) {
                val url = appDB.bookDao().queryFavoriteByUrl(bookModel.url)?.url
                val favorite = appDB.bookDao().queryFavoriteByUrl(bookModel.url)?.favorite
                launch(Dispatchers.Main) {
                    if (TextUtils.isEmpty(url) && favorite != 1) {
                        launch(Dispatchers.IO) {
                            bookModel.favorite = 1
                            appDB.bookDao().inserts(bookModel)
                        }
                        showToast("加入书架成功")
                    } else {
                        showToast("该书籍已在书架中")
                    }
                }
            }
        }
    }
}