package cn.mewlxy.novel.model

import android.text.TextUtils
import android.util.Log
import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import cn.mewlxy.novel.utils.showToast
import com.mewlxy.readlib.interfaces.OnBookSignsListener
import com.mewlxy.readlib.interfaces.OnChaptersListener
import com.mewlxy.readlib.interfaces.OnReadRecordListener
import com.mewlxy.readlib.model.*
import com.mewlxy.readlib.utlis.MD5Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.jsoup.nodes.Document
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * description：
 * author：luoxingyuan
 */
open class BookRepositoryImpl : BookRepository() {
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
                launch(Dispatchers.Main) {
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

                                launch(Dispatchers.IO) {
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

    override fun saveBookRecord(mBookRecord: ReadRecordBean) {
        try {
            uiScope.launch(Dispatchers.IO) {
                mBookRecord.bookMd5 = MD5Utils.strToMd5By16(mBookRecord.bookUrl)!!
                try {
                    appDB.readRecordDao().inserts(ReadRecordModel.createReadRecordModel(mBookRecord))
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }

    override fun getBookRecord(bookUrl: String, readRecordListener: OnReadRecordListener) {
        readRecordListener.onStart()
        var readRecordModel: ReadRecordModel?
        try {
            uiScope.launch(Dispatchers.IO) {
                readRecordModel = appDB.readRecordDao().getReadRecord(MD5Utils.strToMd5By16(bookUrl)!!)
                launch(Dispatchers.Main) {
                    readRecordListener.onSuccess(if (readRecordModel == null) ReadRecordModel() else readRecordModel!!)
                }
            }
        } catch (e: Exception) {
            readRecordListener.onError(e.toString())
        }
    }

    override fun chapterBeans(mCollBook: BookBean, onChaptersListener: OnChaptersListener, start: Int) {
        onChaptersListener.onStart()
        try {
            uiScope.launch(Dispatchers.IO) {
                val chapters = arrayListOf<ChapterBean>()
                chapters.addAll(appDB.chapterDao().getChaptersByBookUrl(mCollBook.url, start = start).map {
                    return@map it.convert2ChapterBean()
                })
                launch(Dispatchers.Main) {
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
                withContext(Dispatchers.Main){
                    if (TextUtils.isEmpty(url) && favorite == null) {
                        launch(Dispatchers.IO) {
                            bookModel.favorite = 1
                            try {
                                appDB.bookDao().inserts(bookModel)
                            } catch (e: Exception) {
                            }
                        }
                        showToast("加入书架成功")
                    } else if (!TextUtils.isEmpty(url) && favorite == 0) {
                        launch(Dispatchers.IO) {
                            bookModel.favorite = 1
                            appDB.bookDao().update(bookModel)
                        }
                        showToast("加入书架成功")
                    } else {
                        showToast("该书籍已在书架中")
                    }
                }
            }
        }
    }

    //---------------------------------------------书签相关---------------------------------------------
    override fun hasSigned(chapterUrl: String): Boolean {
        var bookSign: BookSignModel? = null
        uiScope.launch(Dispatchers.IO) {
            bookSign = appDB.bookSignDao().getSignsByChapterUrl(chapterUrl)
        }
        return bookSign != null
    }

    override fun addSign(mBookUrl: String, chapterUrl: String, chapterName: String, bookSignsListener: OnBookSignsListener) {
        bookSignsListener.onStart()
        val bookSign = BookSignModel()
        bookSign.bookUrl = mBookUrl
        bookSign.chapterUrl = chapterUrl
        bookSign.chapterName = chapterName
        try {
            uiScope.launch(Dispatchers.IO) {
                if (appDB.bookSignDao().getSignsByChapterUrl(chapterUrl) == null) {
                    try {
                        appDB.bookSignDao().inserts(bookSign)
                    } catch (e: Exception) {
                    }
                    launch(Dispatchers.Main) {
                        bookSignsListener.onSuccess(mutableListOf(bookSign))
                    }
                } else {
                    launch(Dispatchers.Main) {
                        showToast("本章节书签已经存在")
                    }
                }
            }
        } catch (e: Exception) {
            bookSignsListener.onError(e.toString())
        }
    }

    override fun deleteSign(vararg bookSign: BookSignTable) {
        uiScope.launch(Dispatchers.IO) {
            val list = bookSign.map {
                return@map it as BookSignModel
            }.toTypedArray()
            appDB.bookSignDao().delete(*list)
        }
    }

    override fun getSigns(bookUrl: String, bookSignsListener: OnBookSignsListener) {
        bookSignsListener.onStart()
        val bookSigns = mutableListOf<BookSignModel>()
        try {
            uiScope.launch(Dispatchers.IO) {
                bookSigns.addAll(appDB.bookSignDao().getSignsByBookUrl(bookUrl))
                launch(Dispatchers.Main) {
                    bookSignsListener.onSuccess(bookSigns)
                }
            }
        } catch (e: Exception) {
            bookSignsListener.onError(e.toString())
        }

    }
}