package cn.mewlxy.novel.ui

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.mewlxy.novel.R
import cn.mewlxy.novel.base.BaseFragment
import cn.mewlxy.novel.constant.Source
import cn.mewlxy.novel.imageloader.ImageLoader
import cn.mewlxy.novel.jsoup.DomSoup
import cn.mewlxy.novel.jsoup.OnJSoupListener
import com.wangpeiyuan.cycleviewpager2.CycleViewPager2Helper
import com.wangpeiyuan.cycleviewpager2.adapter.CyclePagerAdapter
import com.wangpeiyuan.cycleviewpager2.indicator.DotsIndicator
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.search_view.*
import org.jsoup.nodes.Document

/**
 * description：
 * author：luoxingyuan
 *
 */

class HomeFragment private constructor() : BaseFragment(), View.OnClickListener {
val referers = arrayListOf<String>()

    companion object {
        val instance: HomeFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }
    }

    private lateinit var items: ArrayList<String>
    private lateinit var bannerAdapter: MyCyclePagerAdapter

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        tv_search.setOnClickListener(this)

    }

    override fun initData() {
        val domSoup = DomSoup()
        addObserver(domSoup)
        domSoup.getSoup(Source.QUANBEN, object : OnJSoupListener {
            override fun start() {
            }

            override fun success(document: Document) {
//                Log.d("document", document.html())
            }

            override fun failed(errMsg: String) {
                print(errMsg)
            }
        })
        referers.add("https://www.mzitu.com/219871")
        referers.add("https://www.mzitu.com/159145/21")
        referers.add("https://www.mzitu.com/219871/9")
        referers.add("https://www.mzitu.com/202743/3")

        items = ArrayList()
        items.add("https://i5.mmzztt.com/2020/01/12c01.jpg")
        items.add("https://i5.mmzztt.com/2018/11/17b21.jpg")
        items.add("https://i5.mmzztt.com/2020/01/12c09.jpg")
        items.add("https://i5.mmzztt.com/2019/09/12d03.jpg")
//        items.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1578045555830&di=d3b9e9fe59a61f3d3580ab36796fa629&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20181116%2Fedf96b0e2c8f4204a874efaa20ac8eff.jpeg")
//        items.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1578045464979&di=7a321738321a0e7686219cfdb3f6e135&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20180928%2Fc0d556a044e94edfa5342fb796be788e.jpeg")
//        items.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1578045466271&di=a20593d690d08f353bbcaf316b10bd4f&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D1125216755%2C3920626769%26fm%3D214%26gp%3D0.jpg")
//        items.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1578045464976&di=4d478ea8ff3f09a60926e1be17c66490&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20180709%2F447350620f194be88a6bcab5b06c2957.jpg")
        bannerAdapter = MyCyclePagerAdapter()
        CycleViewPager2Helper(banner)
                .setAdapter(bannerAdapter)
                .setMultiplePagerScaleInTransformer(
                        10,
                        10,
                        0.1f
                )
                .setDotsIndicator(
                        10f,
                        Color.RED,
                        Color.WHITE,
                        10f,
                        0,
                        15,
                        0,
                        DotsIndicator.Direction.CENTER
                )
                .setAutoTurning(3000L)
                .build()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_search -> {
                startActivity(Intent(activity, SearchActivity::class.java))
            }
        }
    }

    private inner class MyCyclePagerAdapter : CyclePagerAdapter<PagerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            return PagerViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.banner_pager, parent, false)
            )
        }

        override fun getRealItemCount(): Int = items.size

        override fun onBindRealViewHolder(holder: PagerViewHolder, position: Int) {
            ImageLoader.loadImageWithReferer(this@HomeFragment,holder.ivPager,items[position],referers[position])
//            holder.tvTitle.text = position.toString()
        }
    }

    private inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivPager: ImageView = itemView.findViewById(R.id.iv_pager)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    }
}