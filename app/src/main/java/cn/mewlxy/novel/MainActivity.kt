package cn.mewlxy.novel

import android.Manifest
import android.util.SparseArray
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.mewlxy.novel.base.BaseActivity
import cn.mewlxy.novel.base.BaseFragment
import cn.mewlxy.novel.ui.HomeFragment
import cn.mewlxy.novel.ui.MeFragment
import cn.mewlxy.novel.ui.ShelfFragment
import cn.mewlxy.novel.ui.SquareFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mewlxy.readlib.Constant
import java.io.File

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/10 22:39
 *
 */

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2
    private lateinit var pageChangeCallback: ViewPageChangeCallback
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        viewPager2 = findViewById(R.id.view_pager)
        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.offscreenPageLimit = 3

        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        pageChangeCallback = ViewPageChangeCallback(bottomNavigationView)
        viewPager2.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun initData() {
        if (!hasPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), "应用需要获取手机存储权限，请点击允许")
        }
    }

    override fun permissionsGranted() {
        if (!File(Constant.BOOK_CACHE_PATH).exists()) {
            File(Constant.BOOK_CACHE_PATH).mkdir()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
//            R.id.navigation_home -> {
//                viewPager2.currentItem = 0
//            }

            R.id.navigation_square -> {
                viewPager2.currentItem = 0
            }
            R.id.navigation_shelf -> {
                viewPager2.currentItem = 1
            }
            R.id.navigation_me -> {
                viewPager2.currentItem = 2
            }
        }
        return true
    }

    class ViewPageChangeCallback constructor(var bottomNavigationView: BottomNavigationView) : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            when (position) {
//                0 -> {
//                    bottomNavigationView.selectedItemId = R.id.navigation_home
//                }
                0 -> {
                    bottomNavigationView.selectedItemId = R.id.navigation_square
                }
                1 -> {
                    bottomNavigationView.selectedItemId = R.id.navigation_shelf
                }
                2 -> {
                    bottomNavigationView.selectedItemId = R.id.navigation_me
                }
            }
        }
    }
}

//const val FRAG_HOME: Int = 0
const val FRAG_SQUARE: Int = 0
const val FRAG_SHELF: Int = 1
const val FRAG_ME: Int = 2

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments: SparseArray<BaseFragment> = SparseArray(3)

    init {
//        fragments.append(FRAG_HOME, HomeFragment.instance)
        fragments.append(FRAG_SQUARE, SquareFragment.instance)
        fragments.append(FRAG_SHELF, ShelfFragment.instance)
        fragments.append(FRAG_ME, MeFragment.instance)
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}

