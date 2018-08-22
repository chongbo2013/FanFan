package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.RobotMsg;

/**
 * Created by lyw on 2018-05-10.
 */

public class AddSetEvent extends BaseEvent<RobotMsg> {
    public AddSetEvent(@Nullable String uuid) {
        super(uuid);
    }

    public AddSetEvent(@Nullable String uuid, @NonNull Integer code, @Nullable RobotMsg robotMsg) {
        super(uuid, code, robotMsg);
    }
}
