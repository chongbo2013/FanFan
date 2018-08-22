package com.fanfan.robot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fanfan.robot.model.SpeakBean;
import com.seabreeze.log.Print;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangyuanyuan on 2017/9/29.
 */

public class SpeakService extends Service {

    private LinkedBlockingQueue<SpeakBean> mSpeakQueue;//队列

    private SpeakBinder mSpeakBinder = new SpeakBinder();//binder

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSpeakBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class SpeakBinder extends Binder {

        public void initSpeakManager(Context context) {

            mSpeakQueue = new LinkedBlockingQueue<>();

        }

        public void dispatchSpeak(SpeakBean speak) {
            if (mSpeakQueue != null) {
                if (mSpeakQueue.size() > 1) {
                    mSpeakQueue.clear();
                }
                mSpeakQueue.add(speak);
                Print.e("dispatchSpeak" + speak + "  " + mSpeakQueue.size());
            }
        }

        public SpeakBean getSpeakMorestr() {
            if (!mSpeakQueue.isEmpty()) {
                return mSpeakQueue.poll();
            }
            return null;
        }

        public void clear() {
            if (mSpeakQueue != null) {
                mSpeakQueue.clear();
            }
        }
    }
}
