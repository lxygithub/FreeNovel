package cn.mewlxy.novel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import cn.mewlxy.novel.R
import cn.mewlxy.novel.permissions.PermissionUtil
import pub.devrel.easypermissions.AfterPermissionGranted

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/10 22:34
 *
 */
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner, PermissionUtil.PermissionResultCallbacks {

    private lateinit var loadingView: View

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null)
        setContentView(getLayoutResId())
        initView()
        initData()
    }

    fun showLoading() {
        val decorView = (window.decorView as ViewGroup)
        decorView.addView(loadingView)
    }

    fun dismissLoading() {
        val decorView = (window.decorView as ViewGroup)
        decorView.removeView(loadingView)
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


    @AfterPermissionGranted(PermissionUtil.REQUEST_CODE)
    fun requestPermissions(rationale: String, vararg perms: String) {

        if (PermissionUtil.hasPermissions(*perms)) {
            hasPermissionsDo()
        } else {
            PermissionUtil.requestPermissions(this, rationale, *perms)
        }
    }


    open fun hasPermissionsDo() {}


    open fun somePermissionsDeniedDo() {}

    open fun somePermissionsPermanentlyDeniedDo() {}

    open fun showPermissionDeniedDialog(): Boolean {
        return true
    }

    final override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onPermissionsRequestResult(permissions, grantResults, this)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (PermissionUtil.somePermissionsDenied(this, perms.toTypedArray())) {
            somePermissionsDeniedDo()
        }

        if (PermissionUtil.somePermissionPermanentlyDenied(this, perms)) {
            somePermissionsPermanentlyDeniedDo()
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

}