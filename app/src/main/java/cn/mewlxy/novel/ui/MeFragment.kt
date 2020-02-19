package cn.mewlxy.novel.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import cn.mewlxy.novel.R
import cn.mewlxy.novel.base.BaseFragment
import com.mewlxy.readlib.Constant
import com.mewlxy.readlib.utlis.FileUtils
import com.mewlxy.readlib.utlis.SpUtil
import kotlinx.android.synthetic.main.fragment_me.*
import java.io.File


/**
 * description：
 * author：luoxingyuan
 *
 */

class MeFragment private constructor() : BaseFragment(), View.OnClickListener {

    companion object {
        val instance: MeFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MeFragment()
        }
    }


    override fun getLayoutResId(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {
    }

    override fun initData() {
        switch_volume.isChecked = SpUtil.getBooleanValue("volume_turn_page", true)
        switch_volume.setOnCheckedChangeListener { buttonView, isChecked ->
            SpUtil.setBooleanValue("volume_turn_page", isChecked)
        }
        clear_cache.setOnClickListener(this)
        tv_about.setOnClickListener(this)

        val cacheSize = FileUtils.getDirSize(File(Constant.BOOK_CACHE_PATH)) / 1024
        val unit: String
        unit = if (cacheSize in (0..1024)) {
            "kb"
        } else {
            "MB"
        }
        tv_cache.text = "$cacheSize$unit"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.clear_cache -> {

                AlertDialog.Builder(activity)
                        .setMessage("确定要清除缓存么(将会删除所有已缓存章节)？").setNegativeButton("取消", null)
                        .setPositiveButton("确定") { dialog, which ->
                            FileUtils.deleteFile(Constant.BOOK_CACHE_PATH)
                            tv_cache.text = "0kb"
                        }.show()
            }
            R.id.tv_about -> {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/lxygithub/FreeNovel")))
            }
            else -> {
            }
        }
    }

}