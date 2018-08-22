package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.PersonModify;

/**
 * Created by android on 2017/12/22.
 */

public class PersonModifyEvent extends BaseEvent<PersonModify> {
    public PersonModifyEvent(@Nullable String uuid) {
        super(uuid);
    }

    public PersonModifyEvent(@Nullable String uuid, @NonNull Integer code, @Nullable PersonModify personModify) {
        super(uuid, code, personModify);
    }
}
