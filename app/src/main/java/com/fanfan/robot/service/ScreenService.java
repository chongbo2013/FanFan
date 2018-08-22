package com.fanfan.robot.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.fanfan.novel.utils.system.PowerManagerWakeLock;
import com.fanfan.robot.ui.auxiliary.LockActivity;

/**
 * Created by android on 2018/2/26.
 */

public class ScreenService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // 屏蔽系统的屏保
        KeyguardManager manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("KeyguardLock");
        lock.disableKeyguard();

        // 注册一个监听屏幕开启和关闭的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    BroadcastReceiver screenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {//如果接受到关闭屏幕的广播
                if (!LockActivity.isShow) {
                    //开启屏幕唤醒，常亮
                    PowerManagerWakeLock.acquire(ScreenService.this);
                }
                PowerManagerWakeLock.acquire(ScreenService.this);
                //跳转
                LockActivity.newInstance(ScreenService.this);

                PowerManagerWakeLock.release();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        PowerManagerWakeLock.release();
        unregisterReceiver(screenReceiver);
        ScreenService.this.stopSelf();
    }
}
