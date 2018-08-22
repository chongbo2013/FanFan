package com.fanfan.youtu.api.face.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.GroupIds;

/**
 * Created by android on 2018/1/4.
 */

public class GroupIdsEvent extends BaseEvent<GroupIds> {
    public GroupIdsEvent(@Nullable String uuid) {
        super(uuid);
    }

    public GroupIdsEvent(@Nullable String uuid, @NonNull Integer code, @Nullable GroupIds groupIds) {
        super(uuid, code, groupIds);
    }
}
