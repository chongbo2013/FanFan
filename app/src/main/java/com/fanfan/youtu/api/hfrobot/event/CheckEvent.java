package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.Check;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class CheckEvent extends BaseEvent<Check> {
    public CheckEvent(@Nullable String uuid) {
        super(uuid);
    }

    public CheckEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Check check) {
        super(uuid, code, check);
    }
}
