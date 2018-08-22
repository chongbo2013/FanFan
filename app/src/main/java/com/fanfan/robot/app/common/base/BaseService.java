package com.fanfan.robot.app.common.base;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by android on 2017/12/26.
 */

public abstract class BaseService extends Service {

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onResultEvent(Event event) {
//        Print.e(event);
//
//        if (event.isOk()) {
//            onResultBean(event);
//        } else {
//            onError(event);
//        }
//    }
//
//    protected abstract void onResultBean(Event event);
//
//    protected abstract void onError(Event event);


    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseService.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
