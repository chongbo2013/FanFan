package com.fanfan.robot.app.common.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by android on 2017/12/18.
 */

public class BaseGlideModule implements GlideModule {

    int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    int M = 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        builder.setMemoryCache(new LruResourceCache(10 * M));
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "glide-cache", DEFAULT_DISK_CACHE_SIZE));

    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {

    }
}
