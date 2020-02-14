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
import cn.mewlxy.novel.constant.Constant
import cn.mewlxy.novel.permissions.PermissionUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/10 22:34
 *
 */
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner, PermissionUtil.PermissionResultCallbacks {

    private lateinit var loadingView: View
    private var perms: Array<String> = arrayOf()
    private var rationale = ""

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


    open fun requestPermissions(perms: Array<String>, rationale: String = "应用需要获取权限,请点击允许") {
        this.perms = perms
        this.rationale = rationale
        getPerms()
    }

    open fun permissionsGranted() {}

    open fun hasPermissions(perms: Array<String>): Boolean {
        return EasyPermissions.hasPermissions(this, *perms)
    }

    @AfterPermissionGranted(Constant.PERMISSION_REQUEST_CODE)
    private fun getPerms() {
        if (perms.isNotEmpty()) {
            if (EasyPermissions.hasPermissions(this, *perms)) {
                // Already have permission, do the thing
                permissionsGranted()
            } else { // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        PermissionRequest.Builder(this, Constant.PERMISSION_REQUEST_CODE, *perms)
                                .setRationale(rationale)
                                .setPositiveButtonText("允许")
                                .setNegativeButtonText("取消")
                                .setTheme(R.style.my_fancy_style)
                                .build())
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        permissionsGranted()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}