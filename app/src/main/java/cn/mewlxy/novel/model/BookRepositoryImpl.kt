package cn.mewlxy.novel.model

import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
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

    companion object {
        val instance: BookRepositoryImpl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BookRepositoryImpl()
        }
    }


    override fun saveCollBook(mCollBook: BookBean?) {
    }

    override fun saveBookRecord(mBookRecord: ReadRecordBean?) {
    }

    override fun getBookRecord(bookId: String?): ReadRecordBean {
        return ReadRecordBean()
    }

    override fun saveBookChaptersWithAsync(bookChapterBeanList: MutableList<ChapterBean>?, mCollBook: BookBean?) {
    }

    override fun chapterBeans(mCollBook: BookBean, onChaptersListener: OnChaptersListener) {
        onChaptersListener.onStart()
        try {
            uiScope.launch(Dispatchers.IO) {
                val chapters = arrayListOf<ChapterBean>()
                chapters.addAll(appDB.chapterDao().getChaptersByBookUrl(mCollBook.url,limit = 10).map {
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

    override fun requestChapterContents(mCollBook: BookBean, requestChapters: MutableList<ChapterBean>, onChaptersListener: OnChaptersListener) {
        onChaptersListener.onStart()
        val singleList = requestChapters.map {
            return@map getChapterContent(it)
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
                    }

                    override fun onNext(t: ChapterBean) {
                        newChapters.add(t)
                    }

                    override fun onError(t: Throwable?) {
                        onChaptersListener.onError(t.toString())
                    }

                })

    }


    private fun getChapterContent(chapterBean: ChapterBean): Single<ChapterBean> {
        val single = Single.create<ChapterBean> {
            val chapterContent = appDB.chapterDao().getChapterContent(chapterBean.url)
            if (chapterContent.isNullOrEmpty()) {
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
                        val md5Str = MD5Utils.strToMd5By16(chapterBean.bookUrl)
                        //存储章节内容到本地文件
                        saveChapterInfo(md5Str,chapterBean.name,chapterBean.content)
                        val chapterModel = ChapterModel()
                        chapterModel.id = chapterBean.id
                        chapterModel.name = chapterBean.name
                        chapterModel.url = chapterBean.url
                        chapterModel.content = chapterBean.content
                        chapterModel.bookName = chapterBean.bookName
                        chapterModel.bookUrl = chapterBean.bookUrl
                        uiScope.launch(Dispatchers.IO) {
                            appDB.chapterDao().updates(chapterModel)
                        }
                        it.onSuccess(chapterBean)
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


        return single
    }
}