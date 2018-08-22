package com.fanfan.robot.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by android on 2018/2/23.
 */

public class Channel implements Serializable, MultiItemEntity {

    public static final int TYPE_TITLE = 1;
    public static final int TYPE_CONTENT = 2;

    public int itemtype;

    private String channelName;

    @Override
    public int getItemType() {
        return itemtype;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }
}
