package cn.mewlxy.novel.imageloader

import android.app.Activity
import android.widget.ImageView
import cn.mewlxy.novel.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/17 22:37
 *
 */
class ImageLoader {
    companion object {
        fun loadImage(activity: Activity, imageView: ImageView, imgUrl: String?) {
            Glide.with(activity).load(imgUrl).placeholder(R.drawable.pic_placeholder).error(R.drawable.error_placeholder).into(imageView)
        }

        fun loadImage(fragment: androidx.fragment.app.Fragment, imageView: ImageView, imgUrl: String?) {
            Glide.with(fragment).load(imgUrl)
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.error_placeholder).into(imageView)
        }

        fun loadImageWithReferer(fragment: androidx.fragment.app.Fragment, imageView: ImageView, imgUrl: String?, referer: String) {
            Glide.with(fragment).load(GlideUrl(imgUrl, LazyHeaders.Builder()
                    .addHeader("referer", referer)
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36").build()))
                    .placeholder(R.drawable.pic_placeholder).error(R.drawable.error_placeholder).into(imageView)
        }


        fun loadImage(fragmentActivity: androidx.fragment.app.FragmentActivity, imageView: ImageView, imgUrl: String?) {
            Glide.with(fragmentActivity).load(imgUrl).placeholder(R.drawable.pic_placeholder).error(R.drawable.error_placeholder).into(imageView)
        }
    }
}