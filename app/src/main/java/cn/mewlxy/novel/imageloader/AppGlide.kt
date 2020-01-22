package cn.mewlxy.novel.imageloader

import android.content.Context
import android.util.Log
import cn.mewlxy.novel.R
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.MemoryCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * description：
 * author：luoxingyuan
 * date：2019/7/13 8:29
 *
 */
@GlideModule
public class AppGlide : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {

        var memoryCache = object : MemoryCache {
            override fun put(key: Key, resource: Resource<*>?): Resource<*>? {
                return null
            }

            override fun setSizeMultiplier(multiplier: Float) {
            }

            override fun setResourceRemovedListener(listener: MemoryCache.ResourceRemovedListener) {
            }

            override fun remove(key: Key): Resource<*>? {
                return null
            }

            override fun trimMemory(level: Int) {
            }

            override fun clearMemory() {
            }

            override fun getCurrentSize(): Long {
                return context.cacheDir.totalSpace
            }

            override fun getMaxSize(): Long {
                return 50L * 1024 * 1024 * 1024
            }

        }
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565)
                .error(R.drawable.img_loading_failed)
                .placeholder(R.drawable.img_place_holder)
                .encodeQuality(50)
                .timeout(60 * 1000))
                .setDiskCache(DiskLruCacheFactory(context.cacheDir.absolutePath, 50L * 1024 * 1024 * 1024))
                .setLogLevel(Log.DEBUG)
                .setMemoryCache(memoryCache)

    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

    }
}