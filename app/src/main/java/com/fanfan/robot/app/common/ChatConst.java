package com.fanfan.robot.app.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public class ChatConst {
    public static final String RECYCLER_DATABASE_NAME = "recycler.db";
    public static final int SENDING = 0;
    public static final int COMPLETED = 1;
    public static final int SENDERROR = 2;

    public static final int LOCAL_NOMAL = 3;
    public static final int LOCAL_VIDEO = 4;
    public static final int LOCAL_VOICE = 5;
    public static final int LOCAL_NAVIGATION = 6;
    public static final int DATA = 7;
    public static final int SING = 8;

    @IntDef({SENDING, COMPLETED, SENDERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SendState {
    }

    @IntDef({LOCAL_VIDEO, LOCAL_VOICE, LOCAL_NAVIGATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LocalType {
    }
}
