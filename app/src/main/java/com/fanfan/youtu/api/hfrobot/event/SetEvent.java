package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.SetBean;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class SetEvent extends BaseEvent<SetBean> {

    public SetEvent(@Nullable String uuid) {
        super(uuid);
    }

    public SetEvent(@Nullable String uuid, @NonNull Integer code, @Nullable SetBean setBean) {
        super(uuid, code, setBean);
    }
}
