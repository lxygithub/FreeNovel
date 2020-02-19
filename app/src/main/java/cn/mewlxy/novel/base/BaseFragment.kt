package cn.mewlxy.novel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import cn.mewlxy.novel.R

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/10 22:42
 *
 */
abstract class BaseFragment : androidx.fragment.app.Fragment(), LifecycleOwner {

    private var isDataLoaded = false // 数据是否已请求
    private lateinit var loadingView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(getLayoutResId(), container, false)
        loadingView = LayoutInflater.from(activity).inflate(R.layout.loading_view, null)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun showLoading() {
        val decorView = (activity?.window?.decorView as ViewGroup)
        if (loadingView.parent != decorView) {
            decorView.addView(loadingView)
        }
    }

    fun dismissLoading() {
        val decorView = (activity?.window?.decorView as ViewGroup)
        if (loadingView.parent == decorView) {
            decorView.removeView(loadingView)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            initData()
        }
        isDataLoaded = true
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy() {
        super.onDestroy()

    }

    fun addObserver(observer: LifecycleObserver) {
        lifecycle.addObserver(observer)
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView()

    abstract fun initData()
}