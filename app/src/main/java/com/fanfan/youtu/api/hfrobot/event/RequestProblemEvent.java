package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.RequestProblem;

/**
 * Created by Administrator on 2018/3/28/028.
 */

public class RequestProblemEvent extends BaseEvent<RequestProblem> {
    public RequestProblemEvent(@Nullable String uuid) {
        super(uuid);
    }

    public RequestProblemEvent(@Nullable String uuid, @NonNull Integer code, @Nullable RequestProblem requestProblem) {
        super(uuid, code, requestProblem);
    }
}
