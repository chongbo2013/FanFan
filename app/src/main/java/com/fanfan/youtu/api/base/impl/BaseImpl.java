package com.fanfan.youtu.api.base.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.base.OkhttpManager;
import com.fanfan.youtu.token.YoutuSign;

import java.lang.reflect.ParameterizedType;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by android on 2017/12/21.
 */

public class BaseImpl<Service> {

    private static Retrofit mRetrofit;
    protected Service mService;

    public BaseImpl(@NonNull Context context) {

        initRetrofit(context);

        mService = mRetrofit.create(getServiceClass());
    }


    private void initRetrofit(Context context) {
        if (null != mRetrofit)
            return;

        YoutuSign.init();

        // 配置 Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_YOUTU_BASE)                         // 设置 base url
                .client(OkhttpManager.getInstance().getOkhttpClient())                                     // 设置 client
                .addConverterFactory(GsonConverterFactory.create()) // 设置 Json 转换工具
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    private Class<Service> getServiceClass() {
        return (Class<Service>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private <T> T getApiService(String baseUrl, OkHttpClient client, Class<T> clz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(clz);
    }

}
