package com.fanfan.youtu.api.uploadfile.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.uploadfile.bean.Filezoo;

/**
 * Created by android on 2018/1/4.
 */

public class UpfilesZooEvent extends BaseEvent<Filezoo> {


    public UpfilesZooEvent(@Nullable String uuid) {
        super(uuid);
    }

    public UpfilesZooEvent(@Nullable String uuid, @NonNull Integer code, @Nullable Filezoo filezoo) {
        super(uuid, code, filezoo);
    }
}
