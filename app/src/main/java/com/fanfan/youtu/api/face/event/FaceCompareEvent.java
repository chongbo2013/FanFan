package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceCompare;

/**
 * Created by android on 2017/12/22.
 */

public class FaceCompareEvent extends BaseEvent<FaceCompare> {
    public FaceCompareEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceCompareEvent(@Nullable String uuid, @NonNull Integer code, @Nullable FaceCompare o) {
        super(uuid, code, o);
    }
}
