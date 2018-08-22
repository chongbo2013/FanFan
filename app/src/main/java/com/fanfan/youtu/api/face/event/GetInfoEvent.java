package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.GetInfo;

/**
 * Created by android on 2018/1/4.
 */

public class GetInfoEvent extends BaseEvent<GetInfo> {
    public GetInfoEvent(@Nullable String uuid) {
        super(uuid);
    }

    public GetInfoEvent(@Nullable String uuid, @NonNull Integer code, @Nullable GetInfo getInfo) {
        super(uuid, code, getInfo);
    }
}
