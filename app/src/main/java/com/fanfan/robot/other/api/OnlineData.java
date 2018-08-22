package com.fanfan.robot.other.api;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by android on 2017/12/19.
 */

public class OnlineData {

    private volatile static OnlineData mOnlineData;

    private OnlineData() {
    }


    public static OnlineData getSingleInstance() {
        if (null == mOnlineData) {
            synchronized (OnlineData.class) {
                if (null == mOnlineData) {
                    mOnlineData = new OnlineData();
                }
            }
        }
        return mOnlineData;
    }

    public static OnlineData init(@NonNull Context context, @NonNull final String client_id,
                                  @NonNull final String client_secret) {
        return getSingleInstance();
    }

}
