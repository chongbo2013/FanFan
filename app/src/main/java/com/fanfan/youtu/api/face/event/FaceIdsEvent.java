package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceIds;

/**
 * Created by android on 2018/1/4.
 */

public class FaceIdsEvent extends BaseEvent<FaceIds> {
    public FaceIdsEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceIdsEvent(@Nullable String uuid, @NonNull Integer code, @Nullable FaceIds faceIds) {
        super(uuid, code, faceIds);
    }
}
