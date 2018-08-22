package com.fanfan.youtu.api.baidu.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.fanfan.youtu.api.baidu.bean.Activate;
import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.token.YoutuSign;
import com.fanfan.youtu.utils.GsonUtil;
import com.fanfan.youtu.utils.UUIDGenerator;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class BaiduImpl extends BaseImpl<BaiduService> implements BaiduApi {
    public BaiduImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public Observable<Activate> activateKey(String deviceId, String key) {
        String uuid = UUIDGenerator.getUUID();

        @SuppressLint({"NewApi", "LocalSuppress"})
        ArrayMap<String, Object> array = new ArrayMap<>();

        array.put("deviceId", deviceId);
        array.put("key", key);
        array.put("platformType", 2);
        array.put("version", "3.4.2");

        String arrStr = GsonUtil.GsonString(array);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arrStr);

        return mService.activateKey(body);
    }
}
