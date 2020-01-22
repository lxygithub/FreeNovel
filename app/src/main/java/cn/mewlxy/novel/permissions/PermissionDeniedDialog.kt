package cn.mewlxy.novel.permissions

import android.app.Activity
import pub.devrel.easypermissions.AppSettingsDialog

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/13 18:48
 *
 */
class PermissionDeniedDialog {
    fun show(activity: Activity) {
        show(activity, "权限申请失败", "权限被拒绝使用", "去设置", "取消")
    }

    fun show(activity: Activity, title: String, rationale: String, positiveButton: String, negativeButton: String) {
        AppSettingsDialog.Builder(activity)
                .setTitle(title)
                .setRationale(rationale)
                .setPositiveButton(positiveButton)
                .setNegativeButton(negativeButton)
                .build().show()
    }
}