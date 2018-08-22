package com.fanfan.robot.presenter.ipersenter;

import android.graphics.Bitmap;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.youtu.api.base.event.BaseEvent;

/**
 * Created by android on 2018/1/9.
 */

public abstract class ITakePresenter implements BasePresenter {

    protected ITakeView mBaseView;

    public ITakePresenter(ITakeView baseView) {
        mBaseView = baseView;
    }

    public abstract void startCountDownTimer();

    public abstract void stopCountDownTimer();

    public abstract void deletePhoto(String mSavePath);

    public abstract void sharePhoto(String mSavePath);

    public abstract Bitmap generatingCode(String url, int width, int height, Bitmap logo);

    public interface ITakeView extends BaseView {

        void onTick(String l);

        void onFinish();

        void onError(int code, String msg);

        void onError(BaseEvent event);

        void uploadSuccess(String url);
    }

}