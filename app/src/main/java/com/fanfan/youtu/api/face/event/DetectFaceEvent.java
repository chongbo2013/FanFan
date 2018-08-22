package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.detectFace.DetectFace;

/**
 * Created by android on 2018/1/4.
 */

public class DetectFaceEvent extends BaseEvent<DetectFace> {
    public DetectFaceEvent(@Nullable String uuid) {
        super(uuid);
    }

    public DetectFaceEvent(@Nullable String uuid, @NonNull Integer code, @Nullable DetectFace detectFace) {
        super(uuid, code, detectFace);
    }
}
