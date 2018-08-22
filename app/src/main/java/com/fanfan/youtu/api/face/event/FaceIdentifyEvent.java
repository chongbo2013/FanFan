package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceIdentify;

/**
 * Created by android on 2017/12/21.
 */

public class FaceIdentifyEvent extends BaseEvent<FaceIdentify> {

    public FaceIdentifyEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceIdentifyEvent(@Nullable String uuid, @NonNull Integer code, @Nullable FaceIdentify faceIdentify) {
        super(uuid, code, faceIdentify);
    }

}
