package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FacePersonid;

/**
 * Created by android on 2017/12/21.
 */

public class FacePersonidEvent extends BaseEvent<FacePersonid> {

    public FacePersonidEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FacePersonidEvent(@Nullable String uuid, @NonNull Integer code, @Nullable FacePersonid facePersonid) {
        super(uuid, code, facePersonid);
    }

}
