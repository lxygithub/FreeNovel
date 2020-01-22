package cn.mewlxy.novel.jsoup

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

/**
 * description：
 * author：luoxingyuan
 */
const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"

class DomSoup : DefaultLifecycleObserver {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    fun getSoup(url: String, listener: OnJSoupListener): CoroutineScope {
        listener.start()
        uiScope.launch(Dispatchers.IO) {
            try {
                val document: Document = Jsoup.connect(url).userAgent(UA).header("Host", "www.quanwenyuedu.io")
                        .maxBodySize(0).timeout(60 * 1000).get()
                launch(Dispatchers.Main) {
                    listener.success(document)
                }
            } catch (e: IOException) {
                launch(Dispatchers.Main) {
                    listener.failed(e.toString())
                }
            }
        }
        return uiScope
    }

    fun getResponse(url: String, refrrer: String, listener: OnJSoupResponseListener): CoroutineScope {
        listener.start()
        uiScope.launch(Dispatchers.IO) {
            try {
                val response: Connection.Response = Jsoup.connect(url)
                        .userAgent(UA).method(Connection.Method.GET)
                        .referrer(refrrer)
                        .header("Host", "www.quanwenyuedu.io")
                        .maxBodySize(0).timeout(60 * 1000).execute()
                launch(Dispatchers.Main) {
                    listener.success(response)
                }
            } catch (e: IOException) {
                launch(Dispatchers.Main) {
                    listener.failed(e.toString())
                }
            }
        }
        return uiScope
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancelSoup()
    }


    private fun cancelSoup() {
        if (uiScope.isActive) {
            uiScope.cancel("request canceled")
        }
    }
}


interface OnJSoupListener {
    fun start()
    fun success(document: Document)
    fun failed(errMsg: String)
}

interface OnJSoupResponseListener {
    fun start()
    fun success(resp: Connection.Response)
    fun failed(errMsg: String)
}