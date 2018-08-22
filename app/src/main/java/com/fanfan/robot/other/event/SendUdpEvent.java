package com.fanfan.robot.other.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;

/**
 * Created by android on 2017/12/26.
 */

public class SendUdpEvent extends BaseEvent<String> {

    public SendUdpEvent(@Nullable String uuid) {
        super(uuid);
    }

    public SendUdpEvent(@Nullable String uuid, @NonNull Integer code, @Nullable String s) {
        super(uuid, code, s);
    }

}
