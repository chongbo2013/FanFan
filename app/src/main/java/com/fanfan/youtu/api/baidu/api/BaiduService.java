package com.fanfan.youtu.api.baidu.api;

import com.fanfan.youtu.api.baidu.bean.Activate;
import com.fanfan.youtu.api.base.Constant;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaiduService {


    @POST(Constant.BAIDU_ACTIVATION_API)
    Observable<Activate> activateKey(@Body RequestBody body);

}
