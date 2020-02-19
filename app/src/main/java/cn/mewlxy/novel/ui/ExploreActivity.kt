package cn.mewlxy.novel.ui

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.ExploreAdapter
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.listener.OnItemPositionClickListener
import cn.mewlxy.novel.model.FileModel
import kotlinx.android.synthetic.main.activity_explore.*
import kotlinx.android.synthetic.main.list_view.*
import kotlinx.android.synthetic.main.title_view.*
import java.io.File

/**
 * description：
 * author：luoxingyuan
 */
class ExploreActivity : BaseActivity(), OnItemPositionClickListener<FileModel>, View.OnClickListener {
    private val files = mutableListOf<FileModel>()
    private var topLevelDir = Environment.getExternalStorageDirectory().absolutePath
    private var currentLevelPath = topLevelDir
    private lateinit var adapter: ExploreAdapter
    override fun getLayoutResId(): Int {
        return R.layout.activity_explore
    }

    override fun initView() {
        refresh_layout.isEnabled = false
        recycle_view.layoutManager = LinearLayoutManager(this)
        adapter = ExploreAdapter(this, files)
        adapter.itemPositionClickListener = this
        recycle_view.adapter = adapter
    }

    override fun initData() {
        listFiles(topLevelDir)
        iv_title_back.setOnClickListener(this)
        tv_last_level.setOnClickListener(this)
        tv_title.text = "选择文件"
        tv_last_level.text = "返回上级..  $topLevelDir"
    }


    private fun listFiles(dirPath: String) {
        currentLevelPath = dirPath
        tv_last_level.text = "返回上级..  $dirPath"
        val tempFiles = File(dirPath).listFiles()
        if (tempFiles != null) {
            val listFiles = tempFiles
                    .filter {
                        val filePath = it.absolutePath
                        return@filter it.isDirectory || (filePath.endsWith(".txt")
//                                || filePath.endsWith(".pdf")
//                                || filePath.endsWith(".mobi")
//                                || filePath.endsWith(".epub")
                                )
                    }
                    .map {
                        val fileModel = FileModel()
                        fileModel.name = it.name
                        fileModel.path = it.absolutePath
                        fileModel.isDirectory = it.isDirectory
                        return@map fileModel
                    }.toMutableList().sortedWith(Comparator { t1, t2 ->
                        return@Comparator t1.name.compareTo(t2.name)
                    })

            files.clear()
            files.addAll(listFiles)
            adapter.notifyDataSetChanged()

        }
    }


    override fun itemClick(position: Int, t: FileModel) {
        if (t.isDirectory) {
            listFiles(t.path)
        } else {
            setResult(Activity.RESULT_OK, Intent().putExtra("book_file", t))
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_title_back -> {
                finish()
            }
            R.id.tv_last_level -> {
                if (currentLevelPath != topLevelDir) {
                    val parentPath = currentLevelPath.substring(0, currentLevelPath.lastIndexOf("/"))
                    listFiles(parentPath)
                }
            }
            else -> {
            }
        }
    }
}