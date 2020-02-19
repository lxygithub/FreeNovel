package cn.mewlxy.novel.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.mewlxy.novel.App
import cn.mewlxy.novel.R
import cn.mewlxy.novel.adapter.ShelfAdapter
import cn.mewlxy.novel.appDB
import cn.mewlxy.novel.base.BaseFragment
import cn.mewlxy.novel.listener.OnItemPositionClickListener
import cn.mewlxy.novel.model.BookModel
import cn.mewlxy.novel.model.BookRepositoryImpl
import cn.mewlxy.novel.model.FileModel
import com.mewlxy.readlib.activity.NovelReadActivity
import kotlinx.android.synthetic.main.fragment_shelf.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * description：
 * author：luoxingyuan
 *
 */

class ShelfFragment private constructor() : BaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ShelfAdapter.OnItemDeleteListener<BookModel>, OnItemPositionClickListener<BookModel> {
    private lateinit var adapter: ShelfAdapter
    private val myBooks = ArrayList<BookModel>()
    private val searchResultBooks = ArrayList<BookModel>()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    companion object {
        val instance: ShelfFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ShelfFragment()
        }
    }


    override fun getLayoutResId(): Int {
        return R.layout.fragment_shelf
    }

    override fun initView() {

        iv_title_more.setOnClickListener(this)
        tv_complete.setOnClickListener(this)
        rv_shelf.layoutManager = GridLayoutManager(activity, 3)
        refresh_layout.setColorSchemeResources(R.color.colorAccent)
    }

    override fun initData() {
        adapter = ShelfAdapter(activity as Context, myBooks)
        adapter.onItemDeleteListener = this
        adapter.itemPositionClickListener = this
        rv_shelf.adapter = adapter
        refresh_layout.setOnRefreshListener(this)

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    rv_shelf.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    search(s.toString().trim())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        fetchData(true)
    }

    private fun fetchData(refresh: Boolean) {
        if (!refresh) {
            showLoading()
        }
        uiScope.launch(Dispatchers.IO) {
            if (refresh) {
                myBooks.clear()
            }
            myBooks.addAll(appDB.bookDao().getAllFavorite())
            launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
                if (!refresh) {
                    dismissLoading()
                }
                refresh_layout.isRefreshing = false
            }
        }
    }

    private fun search(keyword: String) {
        showLoading()
        uiScope.launch(Dispatchers.IO) {
            searchResultBooks.addAll(appDB.bookDao().findByName(keyword))
            launch(Dispatchers.Main) {
                val searchAdapter = ShelfAdapter(activity as Context, searchResultBooks)
                rv_shelf.adapter = searchAdapter
                searchAdapter.notifyDataSetChanged()
                dismissLoading()
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_title_more -> {
                val popupWindow = PopupMenu(activity, iv_title_more)
                popupWindow.inflate(R.menu.shelf_pop_menu)

                popupWindow.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.shelf_manage -> {
                            tv_complete.visibility = View.VISIBLE
                            adapter.setManageMode(true)
                        }
                        R.id.shelf_add -> {
                            startActivityForResult(Intent(activity, ExploreActivity::class.java), 1)

                        }

                    }
                    return@setOnMenuItemClickListener true
                }
                popupWindow.show()
            }
            R.id.tv_complete -> {
                tv_complete.visibility = View.GONE
                adapter.setManageMode(false)
            }
        }
    }

    override fun onRefresh() {
        fetchData(true)
    }

    override fun deleteItem(t: BookModel) {
        showLoading()
        uiScope.launch(Dispatchers.IO) {
            t.favorite = 0
            appDB.bookDao().update(t)
            launch(Dispatchers.Main) {
                myBooks.remove(t)
                adapter.notifyDataSetChanged()
                dismissLoading()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val bookModel = BookModel()
            val fileModel = data?.getSerializableExtra("book_file") as FileModel
            bookModel.name = fileModel.name
            bookModel.bookFilePath = fileModel.path
            bookModel.favorite = 1
            uiScope.launch(Dispatchers.IO) {
                try {
                    appDB.bookDao().inserts(bookModel)
                } catch (e: Exception) {
                }
                launch(Dispatchers.Main) {
                    fetchData(true)
                }
            }
        }
    }

    override fun itemClick(position: Int, t: BookModel) {
        if (t.url.isNotBlank()) {
            startActivity(Intent(activity, BookDetailActivity::class.java)
                    .putExtra("name", t.name)
                    .putExtra("bookUrl", t.url))
        } else {
            val bookBean = t.convert2BookBean()
            bookBean.isLocal = 1
            NovelReadActivity.start(context as Activity, bookBean, BookRepositoryImpl.instance)
        }
    }

}