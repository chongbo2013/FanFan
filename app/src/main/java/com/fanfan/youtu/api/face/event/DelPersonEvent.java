package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.Delperson;

/**
 * Created by android on 2017/12/25.
 */

public class DelPersonEvent extends BaseEvent<Delperson> {
    public DelPersonEvent(@Nullable String uuid) {
        super(uuid);
    }

    public DelPersonEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Delperson delperson) {
        super(uuid, code, delperson);
    }
}
