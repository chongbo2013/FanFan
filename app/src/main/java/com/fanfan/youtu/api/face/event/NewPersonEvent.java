package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.Newperson;

/**
 * Created by android on 2017/12/22.
 */

public class NewPersonEvent extends BaseEvent<Newperson> {
    public NewPersonEvent(@Nullable String uuid) {
        super(uuid);
    }

    public NewPersonEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Newperson ytNewperson) {
        super(uuid, code, ytNewperson);
    }
}
