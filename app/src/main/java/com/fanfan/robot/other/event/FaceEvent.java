package com.fanfan.robot.other.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;

/**
 * Created by android on 2018/2/26.
 */

public class FaceEvent extends BaseEvent<Integer> {
    public FaceEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Integer integer) {
        super(uuid, code, integer);
    }
}
