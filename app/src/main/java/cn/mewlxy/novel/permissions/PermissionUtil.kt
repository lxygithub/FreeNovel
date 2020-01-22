package cn.mewlxy.novel.permissions

import android.app.Activity
import androidx.fragment.app.Fragment
import cn.mewlxy.novel.App
import pub.devrel.easypermissions.EasyPermissions

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/13 18:03
 *
 */
class PermissionUtil {


    companion object {

        const val REQUEST_CODE = 2333

        fun hasPermissions(vararg perms: String): Boolean {
            return EasyPermissions.hasPermissions(App.app, *perms)
        }

        fun requestPermissions(activity: Activity, rationale: String, vararg perms: String) {
            EasyPermissions.requestPermissions(activity, rationale, REQUEST_CODE, *perms)
        }

        fun requestPermissions(fragment: androidx.fragment.app.Fragment, rationale: String, vararg perms: String) {
            EasyPermissions.requestPermissions(fragment, rationale, REQUEST_CODE, *perms)
        }


        fun somePermissionsDenied(activity: Activity, perms: Array<String>):Boolean {
            return EasyPermissions.somePermissionDenied(activity, *perms)
        }

        fun somePermissionsDenied(fragment: androidx.fragment.app.Fragment, perms: Array<String>):Boolean {
            return EasyPermissions.somePermissionDenied(fragment, *perms)
        }

        fun somePermissionPermanentlyDenied(activity: Activity, perms: MutableList<String>):Boolean {
            return EasyPermissions.somePermissionPermanentlyDenied(activity, perms)
        }

        fun somePermissionPermanentlyDenied(fragment: androidx.fragment.app.Fragment, perms: MutableList<String>):Boolean {
            return EasyPermissions.somePermissionPermanentlyDenied(fragment, perms)
        }

        fun onPermissionsRequestResult(perms: Array<out String>, grantResult: IntArray, vararg receivers: Any) {
            EasyPermissions.onRequestPermissionsResult(REQUEST_CODE, perms, grantResult, receivers)
        }


    }

    interface PermissionResultCallbacks : EasyPermissions.PermissionCallbacks
}