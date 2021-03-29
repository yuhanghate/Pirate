package com.yuhang.novel.pirate.app

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.yuhang.novel.pirate.repository.network.Http
import java.io.InputStream


//@GlideModule
//class HttpGlideModule: AppGlideModule() {
//
//    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//        // 注意这里用我们刚才现有的Client实例传入即可
//        registry.replace(
//            GlideUrl::class.java,
//            InputStream::class.java,
//            OkHttpUrlLoader.Factory(Http.okGlide)
//        )
//    }
//}