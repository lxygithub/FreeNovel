package com.mewlxy.readlib.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View.*
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.mewlxy.readlib.Constant
import com.mewlxy.readlib.Constant.ResultCode.Companion.RESULT_IS_COLLECTED
import com.mewlxy.readlib.R
import com.mewlxy.readlib.adapter.MarkAdapter
import com.mewlxy.readlib.page.ReadSettingManager
import com.mewlxy.readlib.dialog.ReadSettingDialog
import com.mewlxy.readlib.adapter.CategoryAdapter
import com.mewlxy.readlib.base.NovelBaseActivity
import com.mewlxy.readlib.interfaces.OnChaptersListener
import com.mewlxy.readlib.model.*
import com.mewlxy.readlib.page.PageLoader
import com.mewlxy.readlib.page.PageView
import com.mewlxy.readlib.utlis.*
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.layout_download.*
import kotlinx.android.synthetic.main.layout_light.*
import kotlinx.android.synthetic.main.layout_read_mark.*
import java.util.*

/**
 * 阅读页📕
 */
open class NovelReadActivity : NovelBaseActivity() {

    private lateinit var mCategoryAdapter: CategoryAdapter
    private val mChapters = mutableListOf<ChapterBean>()
    private lateinit var mCurrentChapter: ChapterBean //当前章节
    private var currentChapter = 0
    private lateinit var mMarkAdapter: MarkAdapter
    private val mMarks = ArrayList<BookSignTable>()
    private lateinit var mPageLoader: PageLoader
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null

    private lateinit var mSettingDialog: ReadSettingDialog
    private var isCollected = false // isFromSDCard
    private var isNightMode = false
    private var isFullScreen = false

    private lateinit var mCollBook: BookBean
    private lateinit var mBookId: String

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WHAT_CATEGORY -> rlv_list.setSelection(mPageLoader.chapterPos)
                WHAT_CHAPTER -> mPageLoader.openChapter()
            }
        }
    }


    override val layoutId: Int get() = R.layout.activity_read

    // 接收电池信息和时间更新的广播
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Objects.requireNonNull(intent.action) == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra("level", 0)
                mPageLoader.updateBattery(level)
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                mPageLoader.updateTime()
            }// 监听分钟的变化
        }
    }


    override fun initView() {
        mCollBook = intent.getSerializableExtra(EXTRA_COLL_BOOK) as BookBean
        isCollected = intent.getBooleanExtra(EXTRA_IS_COLLECTED, false)
        mBookId = mCollBook.url
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            read_pv_page.setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //获取页面加载器
        mPageLoader = read_pv_page.getPageLoader(mCollBook, bookRepository)

        mSettingDialog = ReadSettingDialog(this, mPageLoader)
        //禁止滑动展示DrawerLayout
        read_dl_slide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //侧边打开后，返回键能够起作用
        read_dl_slide.isFocusableInTouchMode = false
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this)
        //隐藏StatusBar
        read_pv_page.post { this.hideSystemBar() }
        read_abl_top_menu.setPadding(0, ScreenUtils.statusBarHeight, 0, 0)
        ll_download.setPadding(0, ScreenUtils.statusBarHeight, 0, ScreenUtils.dpToPx(15))

        val lp = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = 1
        }
        window.attributes = lp

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto) {
            BrightnessUtils.setDefaultBrightness(this)
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().brightness)
        }

        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReceiver, intentFilter)

        if (!SpUtil.getBooleanValue(Constant.BookGuide, false)) {
            iv_guide.visibility = VISIBLE
            toggleMenu(false)
        }

        bookRepository.chapterBeans(mCollBook, object : OnChaptersListener {
            override fun onStart() {
            }


            override fun onSuccess(chapterBeans: MutableList<ChapterBean>) {
                mPageLoader.collBook.chapters = chapterBeans
                mPageLoader.refreshChapterList()
            }

            override fun onError(errorMsg: String) {
            }

        })


    }

    override fun initData() {
        tv_book_name.text = mCollBook.name
        mCategoryAdapter = CategoryAdapter()
        rlv_list.adapter = mCategoryAdapter
        rlv_list.isFastScrollEnabled = true
        rlv_mark.layoutManager = LinearLayoutManager(this)
        mMarkAdapter = MarkAdapter(mMarks)
        rlv_mark.adapter = mMarkAdapter
        isNightMode = ReadSettingManager.getInstance().isNightMode
        //夜间模式按钮的状态
        toggleNightMode()
        isFullScreen = ReadSettingManager.getInstance().isFullScreen
        toolbar.setNavigationOnClickListener { finish() }
        read_setting_sb_brightness.progress = ReadSettingManager.getInstance().brightness
        mPageLoader.setOnPageChangeListener(
                object : PageLoader.OnPageChangeListener {

                    override fun onChapterChange(pos: Int) {
                        var index: Int = pos
                        if (pos >= mChapters.size) {
                            index = mChapters.size - 1
                        }
                        mCategoryAdapter.setChapter(index)
                        mCurrentChapter = mChapters[index]
                        currentChapter = index
                    }

                    override fun chapterContents(requestChapters: List<ChapterBean>) {
                        bookRepository.requestChapterContents(mCollBook, requestChapters, object : OnChaptersListener {
                            override fun onStart() {
                            }

                            override fun onSuccess(chapterBeans: MutableList<ChapterBean>) {
                                mHandler.sendEmptyMessage(WHAT_CATEGORY)
                            }

                            override fun onError(errorMsg: String) {
                            }
                        })
                    }

                    override fun onChaptersFinished(chapters: List<ChapterBean>) {
                        mChapters.clear()
                        mChapters.addAll(chapters)
                        mCategoryAdapter.refreshItems(mChapters)
                    }

                    override fun onPageCountChange(count: Int) {}

                    override fun onPageChange(pos: Int) {

                    }
                }
        )
        read_pv_page.setTouchListener(object : PageView.TouchListener {
            override fun onTouch(): Boolean {
                return !hideReadMenu()
            }

            override fun center() {
                toggleMenu(true)
            }

            override fun prePage() {}

            override fun nextPage() {}

            override fun cancel() {}
        })
        read_tv_category.setOnClickListener {
            //移动到指定位置
            if (mCategoryAdapter.count > 0) {
                rlv_list.setSelection(mPageLoader.chapterPos)
            }
            //切换菜单
            toggleMenu(true)
            //打开侧滑动栏
            read_dl_slide.openDrawer(GravityCompat.START)
        }
        tv_light.setOnClickListener {
            ll_light.visibility = GONE
            rlReadMark.visibility = GONE
            if (isVisible(ll_light)) {
                ll_light.visibility = GONE
            } else {
                ll_light.visibility = VISIBLE
            }
        }
        tv_setting.setOnClickListener {
            ll_light.visibility = GONE
            rlReadMark.visibility = GONE
            toggleMenu(false)
            mSettingDialog.show()
        }

        read_setting_sb_brightness.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(this@NovelReadActivity, progress)
                //存储亮度的进度条
                ReadSettingManager.getInstance().brightness = progress
            }
        })

        tvBookReadMode.setOnClickListener {
            isNightMode = !isNightMode
            mPageLoader.setNightMode(isNightMode)
            toggleNightMode()
        }

        read_tv_brief.setOnClickListener {
            //            val intent = Intent(this, NovelBookDetailActivity::class.java)
//            intent.putExtra(Constant.Bundle.BookId, Integer.valueOf(mBookId))
//            startActivity(intent)
        }

        read_tv_community.setOnClickListener {
            if (isVisible(read_ll_bottom_menu)) {
                if (isVisible(rlReadMark)) {
                    gone(rlReadMark)
                } else {
                    gone(ll_light)
                    updateMark()
                    visible(rlReadMark)
                }
            }
        }

        tvAddMark.setOnClickListener {
            mMarkAdapter.edit = false
//            if (bookRepository().getSignById(mCurrentChapter.chapterId)) {
//                showToast(getString(R.string.sign_exist))
//                return@setOnClickListener
//            }
//            bookRepository().addSign(mBookId, mCurrentChapter.chapterId, mCurrentChapter.title)
            updateMark()
        }

        tvClear.setOnClickListener {
            if (mMarkAdapter.edit) {
                val sign = mMarkAdapter.selectList
                if (sign != "") {
//                    bookRepository().deleteSign(sign)
                    updateMark()
                }
                mMarkAdapter.edit = false
            } else {
                mMarkAdapter.edit = true
            }
        }

        tv_cache.setOnClickListener {
            if (!isCollected) { //没有收藏 先收藏 然后弹框
                //设置为已收藏
                isCollected = true
                //设置阅读时间
                mCollBook.lastRead = System.currentTimeMillis().toString()
//                bookRepository().saveCollBookWithAsync(mCollBook)
            }
            showDownLoadDialog()

        }
        rlv_list.setOnItemClickListener { _, _, position, _ ->
            read_dl_slide.closeDrawer(GravityCompat.START)
            mPageLoader.skipToChapter(position)
        }
        iv_guide.setOnClickListener {
            iv_guide.visibility = GONE
            SpUtil.setBooleanValue(Constant.BookGuide, true)
        }
    }

    private fun showDownLoadDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.d_cache_num))
                .setItems(
                        arrayOf(
                                getString(R.string.d_cache_last_50),
                                getString(R.string.d_cache_last_all),
                                getString(R.string.d_cache_all)
                        )
                ) { _, which ->
                    when (which) {
                        0 -> {
                            //50章
                            val last = currentChapter + 50
                            if (last > mCollBook.chapters.size) {
                                downLoadCache(mCollBook.chapters, mCollBook.chapters.size)
                            } else {
                                downLoadCache(mCollBook.chapters, last)
                            }
                        }
                        1 -> {
                            //后面所有
                            val lastBeans = ArrayList<ChapterBean>()
                            for (i in currentChapter until mCollBook.chapters.size) {
                                lastBeans.add(mCollBook.chapters[i])
                            }
                            downLoadCache(lastBeans, mCollBook.chapters.size - currentChapter)
                        }
                        2 -> downLoadCache(mCollBook.chapters, mCollBook.chapters.size) //所有
                        else -> {
                        }
                    }
                    toggleMenu(true)
                }
        builder.show()
    }

    private fun downLoadCache(beans: List<ChapterBean>, size: Int) {

    }

    private fun toggleNightMode() {
        if (isNightMode) {
            tvBookReadMode.text = resources.getString(R.string.book_read_mode_day)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_moring)
            tvBookReadMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            cl_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.read_bg_night))
        } else {
            tvBookReadMode.text = resources.getString(R.string.book_read_mode_day)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night)
            tvBookReadMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            cl_layout.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            ReadSettingManager.getInstance().pageStyle.bgColor
                    )
            )
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private fun hideReadMenu(): Boolean {
        hideSystemBar()
        if (read_abl_top_menu.visibility == VISIBLE) {
            toggleMenu(true)
            return true
        } else if (mSettingDialog.isShowing) {
            mSettingDialog.dismiss()
            return true
        }
        return false
    }

    private fun showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this)
        }
    }

    private fun hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this)
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean) {
        initMenuAnim()
        gone(ll_light, rlReadMark)
        if (read_abl_top_menu.visibility == VISIBLE) {
            //关闭
            read_abl_top_menu.startAnimation(mTopOutAnim)
            read_ll_bottom_menu.startAnimation(mBottomOutAnim)
            read_abl_top_menu.visibility = GONE
            read_ll_bottom_menu.visibility = GONE

            if (hideStatusBar) {
                hideSystemBar()
            }
        } else {
            read_abl_top_menu.visibility = VISIBLE
            read_ll_bottom_menu.visibility = VISIBLE
            read_abl_top_menu.startAnimation(mTopInAnim)
            read_ll_bottom_menu.startAnimation(mBottomInAnim)

            showSystemBar()
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {
        if (mTopInAnim != null) return
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim!!.duration = 200
        mBottomOutAnim!!.duration = 200
    }


    private fun updateMark() {
        mMarks.clear()
//        mMarks.addAll(bookRepository().getSign(mBookId))
        mMarkAdapter.notifyDataSetChanged()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> return mPageLoader.skipToPrePage()

            KeyEvent.KEYCODE_VOLUME_DOWN -> return mPageLoader.skipToNextPage()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (read_abl_top_menu.visibility == VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen) {
                toggleMenu(true)
                return
            }
        } else if (mSettingDialog.isShowing) {
            mSettingDialog.dismiss()
            return
        } else if (read_dl_slide.isDrawerOpen(GravityCompat.START)) {
            read_dl_slide.closeDrawer(GravityCompat.START)
            return
        }
        Log.e(TAG, "onBackPressed: " + mCollBook.chapters.isEmpty())

        if (!mCollBook.isLocal && !isCollected && mCollBook.chapters.isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_book))
                    .setMessage(getString(R.string.like_book))
                    .setPositiveButton(getString(R.string.sure)) { dialog, which ->
                        //设置为已收藏
                        isCollected = true
                        //设置阅读时间
                        mCollBook.lastRead = System.currentTimeMillis().toString()

//                        bookRepository().saveCollBookWithAsync(mCollBook)

                        exit()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which -> exit() }.create()
            alertDialog.show()
        } else {
            exit()
        }
    }

    // 退出
    private fun exit() {
        // 返回给BookDetail。
        val result = Intent()
        result.putExtra(RESULT_IS_COLLECTED, isCollected)
        setResult(Activity.RESULT_OK, result)
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        if (isCollected) {
            mPageLoader.saveRecord()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPageLoader.closeBook()
        unregisterReceiver(mReceiver)
    }

//    override fun onDownloadChange(pos: Int, status: Int, msg: String) {
//        Log.e(TAG, "onDownloadChange: $pos $status $msg")
//
//        if (msg == getString(R.string.download_success) || msg == getString(R.string.download_error)) {
//            //下载成功或失败后隐藏下载视图
//            if (ll_download != null) {
//                ll_download.visibility = GONE
//                showToast(msg)
//            }
//        } else {
//            if (ll_download != null) {
//                ll_download.visibility = VISIBLE
//                tv_progress.text = ""
//                pb_loading.max = 0
//                pb_loading.progress = 0
//            }
//        }
//    }


//    override fun onDownloadResponse(pos: Int, status: Int) {
//        Log.e(TAG, "onDownloadResponse: $pos $status")
//    }
//
//    @Subscribe
//    fun onDownLoadEvent(message: DownloadMessage) {
//        showToast(message.message)
//    }

    companion object {

        private lateinit var bookRepository: BookRepository
        private const val TAG = "NovelReadActivity"
        const val EXTRA_COLL_BOOK = "extra_coll_book"
        const val EXTRA_IS_COLLECTED = "extra_is_collected"
        private const val WHAT_CATEGORY = 1
        private const val WHAT_CHAPTER = 2

        fun start(context: Context, collBookBean: BookBean, bookRepository: BookRepository) {
            this.bookRepository = bookRepository
            context.startActivity(Intent(context, NovelReadActivity::class.java)
                    .putExtra(EXTRA_COLL_BOOK, collBookBean))
        }
    }
}
