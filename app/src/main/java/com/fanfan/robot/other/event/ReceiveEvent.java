package com.fanfan.robot.other.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;

import java.net.DatagramPacket;

/**
 * Created by android on 2017/12/26.
 */

public class ReceiveEvent extends BaseEvent<DatagramPacket> {

    public ReceiveEvent(@Nullable String uuid) {
        super(uuid);
    }

    public ReceiveEvent(@Nullable String uuid, @NonNull Integer code, @Nullable DatagramPacket datagramPacket) {
        super(uuid, code, datagramPacket);
    }

}
