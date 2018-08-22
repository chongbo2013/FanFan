package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.AddFace;

/**
 * Created by android on 2018/1/4.
 */

public class AddPersonEvent extends BaseEvent<AddFace> {
    public AddPersonEvent(@Nullable String uuid) {
        super(uuid);
    }

    public AddPersonEvent(@Nullable String uuid, @NonNull Integer code, @Nullable AddFace addFace) {
        super(uuid, code, addFace);
    }
}
