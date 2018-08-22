package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.Faceverify;

/**
 * Created by android on 2018/1/4.
 */

public class FaceverifyEvent extends BaseEvent<Faceverify> {
    public FaceverifyEvent(@Nullable String uuid) {
        super(uuid);
    }

    public FaceverifyEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Faceverify faceverify) {
        super(uuid, code, faceverify);
    }
}
