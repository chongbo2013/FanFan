package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.DelFace;

/**
 * Created by android on 2018/1/4.
 */

public class DelFaceEvent extends BaseEvent<DelFace> {
    public DelFaceEvent(@Nullable String uuid) {
        super(uuid);
    }

    public DelFaceEvent(@Nullable String uuid, @NonNull Integer code, @Nullable DelFace delFace) {
        super(uuid, code, delFace);
    }
}
