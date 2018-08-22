package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceInfo;

/**
 * Created by android on 2018/1/4.
 */

public class FaceinfoEvent extends BaseEvent<FaceInfo> {
    public FaceinfoEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceinfoEvent(@Nullable String uuid, @NonNull Integer code, @Nullable FaceInfo faceinfo) {
        super(uuid, code, faceinfo);
    }
}
