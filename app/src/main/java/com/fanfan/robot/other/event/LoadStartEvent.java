package com.fanfan.robot.other.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;

public class LoadStartEvent extends BaseEvent<Boolean> {
    public LoadStartEvent(@Nullable String uuid) {
        super(uuid);
    }

    public LoadStartEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Boolean aBoolean) {
        super(uuid, code, aBoolean);
    }
}
