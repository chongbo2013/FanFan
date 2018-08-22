package com.fanfan.robot.other.music;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.fanfan.robot.listener.music.EventCallback;
import com.fanfan.robot.service.PlayService;

/**
 * Created by android on 2018/1/10.
 */

public class QuitTimer {

    private PlayService mPlayService;
    private EventCallback<Long> mTimerCallback;
    private Handler mHandler;

    private long mTimerRemain;

    public static QuitTimer getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(@NonNull PlayService playService, @NonNull Handler handler, @NonNull EventCallback<Long> timerCallback) {
        mPlayService = playService;
        mHandler = handler;
        mTimerCallback = timerCallback;
    }


    public void stop() {
        mHandler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            mTimerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (mTimerRemain > 0) {
                mTimerCallback.onEvent(mTimerRemain);
                mHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                mPlayService.quit();
            }
        }
    };

}
