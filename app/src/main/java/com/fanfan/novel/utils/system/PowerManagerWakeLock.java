package com.fanfan.novel.utils.system;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by android on 2018/2/26.
 */

public class PowerManagerWakeLock {

    private static PowerManager.WakeLock wakeLock;

    /**
     * 开启 保持屏幕唤醒
     */
    public static void acquire(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PowerManagerWakeLock");
        wakeLock.acquire();
    }

    /**
     * 关闭 保持屏幕唤醒
     */
    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
