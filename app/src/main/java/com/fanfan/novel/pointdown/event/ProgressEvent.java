package com.fanfan.novel.pointdown.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.youtu.api.base.event.BaseEvent;

public class ProgressEvent extends BaseEvent<Progress> {
    public ProgressEvent(@Nullable String uuid) {
        super(uuid);
    }

    public ProgressEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Progress progress) {
        super(uuid, code, progress);
    }
}
