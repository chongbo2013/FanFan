package com.fanfan.robot.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.presenter.ipersenter.IScreenPresenter;

/**
 * Created by android on 2018/2/26.
 */

public class ScreenPresenter extends IScreenPresenter {

    public final static int MSG_SHOW_TIPS = 0x01;

    private ISreenView mSreenView;

    private SreenHandler mSreenHandler;

    private Runnable tipsShowRunable = new Runnable() {

        @Override
        public void run() {
            mSreenHandler.obtainMessage(MSG_SHOW_TIPS).sendToTarget();
        }
    };

    public ScreenPresenter(ISreenView baseView) {
        super(baseView);
        mSreenView = baseView;

        mSreenHandler = new SreenHandler();
    }

    @Override
    public void startTipsTimer() {
        mSreenHandler.postDelayed(tipsShowRunable, Constants.lockingTime);
    }

    @Override
    public void endTipsTimer() {
        mSreenHandler.removeCallbacks(tipsShowRunable);
    }

    @Override
    public void resetTipsTimer() {
        mSreenHandler.removeCallbacks(tipsShowRunable);
        mSreenHandler.postDelayed(tipsShowRunable, Constants.lockingTime);
    }

    @SuppressLint("HandlerLeak")
    public class SreenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_TIPS:
                    mSreenView.showTipsView();
                    break;
            }

        }

    }
}
