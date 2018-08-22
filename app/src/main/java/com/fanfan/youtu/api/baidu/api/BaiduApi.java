package com.fanfan.youtu.api.baidu.api;

import com.fanfan.youtu.api.baidu.bean.Activate;
import com.fanfan.youtu.api.base.Constant;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaiduApi {


    Observable<Activate> activateKey(String deviceId, String key);

}
